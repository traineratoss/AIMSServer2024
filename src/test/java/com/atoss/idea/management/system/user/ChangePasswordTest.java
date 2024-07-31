package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.controller.UserController;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.ChangePasswordDTO;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class ChangePasswordTest {
    UserController spyUserController;

    UserService spyUserService;

    @Mock
    SendEmailService mockSendEmailService;

    @Mock
    UserRepository mockUserRepository;

    @Spy
    ModelMapper spyModelMapper;

    @Mock
    AvatarRepository mockAvatarRepository;


    String bcryptSalt;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        spyUserService = spy(
                new UserServiceImpl(
                        mockUserRepository,
                        spyModelMapper,
                        mockSendEmailService,
                        mockAvatarRepository
                )
        );
        spyUserController = spy(new UserController(spyUserService, mockSendEmailService));
        bcryptSalt = "$2a$10$QkRidA35ea0Fzm/ObrOEgO";
    }

    @Test
    void testChangePassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();

        changePasswordDTO.setUsername("username");
        changePasswordDTO.setNewPassword("newpass");
        changePasswordDTO.setOldPassword("oldpass");

        User user = new User();
        user.setUsername(changePasswordDTO.getUsername());
        user.setPassword(BCrypt.hashpw(changePasswordDTO.getOldPassword(), bcryptSalt));
        user.setIsFirstLogin(false);

        ReflectionTestUtils.setField(spyUserService, "bcryptSalt", bcryptSalt);

        Mockito.when(mockUserRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        Mockito.when(mockUserRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<Object> response = spyUserController.changePassword(changePasswordDTO);

        Mockito.verify(mockUserRepository).save(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangePasswordIncorrectOldPassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();

        changePasswordDTO.setUsername("username");
        changePasswordDTO.setNewPassword("newpass");
        changePasswordDTO.setOldPassword("oldpass");

        User user = new User();
        user.setUsername(changePasswordDTO.getUsername());
        user.setPassword(BCrypt.hashpw(changePasswordDTO.getOldPassword() + "a", bcryptSalt));
        user.setIsFirstLogin(false);

        ReflectionTestUtils.setField(spyUserService, "bcryptSalt", bcryptSalt);

        Mockito.when(mockUserRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        Mockito.when(mockUserRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<Object> response = spyUserController.changePassword(changePasswordDTO);

        Mockito.verify(spyUserController).changePassword(changePasswordDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
