package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.controller.UserController;
import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class UpdateUserTest {
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

    private UserUpdateDTO userUpdateDTO;
    private User user;
    private String username = "username";

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

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUpdatedImage(false);
        user = new User();
    }

    @Test
    void testUpdateUsername() {
        String username = "username";
        userUpdateDTO.setUsername("newusername");
        user.setUsername("newusername");

        Mockito.when(mockUserRepository.findByUsername(userUpdateDTO.getUsername())).thenReturn(Optional.empty());

        UserResponseDTO responseDTO = updateUser(user, username, userUpdateDTO);

        assertEquals(userUpdateDTO.getUsername(), responseDTO.getUsername());
    }

    @Test
    void testUpdateUsernameWithExistentUsername() {
        userUpdateDTO.setUsername("username");
        user.setUsername("username");

        Mockito.when(mockUserRepository.findByUsername(userUpdateDTO.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistException.class, () -> updateUser(user, username, userUpdateDTO));
    }

    @Test
    void testUpdateEmail() {
        String email = "email-old@email.email";
        userUpdateDTO.setEmail("email@email.email");
        user.setEmail("email@email.email");

        Mockito.when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserResponseDTO responseDTO = updateUser(user, username, userUpdateDTO);

        assertEquals(responseDTO.getEmail(), userUpdateDTO.getEmail());
    }

    @Test
    void testUpdateEmailWithExistingEmail() {
        String email = "email@email.email";
        userUpdateDTO.setEmail("email@email.email");
        user.setEmail("email@email.email");

        Mockito.when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> updateUser(user, username, userUpdateDTO));
    }

    @Test
    void testUpdateAvatar() {
        Avatar avatar = new Avatar();
        avatar.setId(1L);
        userUpdateDTO.setAvatarId(1L);

        Mockito.when(mockAvatarRepository.findById(any(Long.class))).thenReturn(Optional.of(avatar));

        UserResponseDTO responseDTO = updateUser(user, username, userUpdateDTO);

        assertEquals(userUpdateDTO.getAvatarId(), responseDTO.getAvatar().getId());
    }

    @Test
    void testUpdateAvatarWithInvalidAvatar() {
        userUpdateDTO.setAvatarId(1L);

        Mockito.when(mockAvatarRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(AvatarNotFoundException.class, () -> updateUser(user, username, userUpdateDTO));
    }

    @Test
    void testAddFullName() {
        userUpdateDTO.setFullName("Full name");
        UserResponseDTO responseDTO = updateUser(user, username, userUpdateDTO);
        assertEquals(userUpdateDTO.getFullName(), responseDTO.getFullName());
    }

    private UserResponseDTO updateUser(User user, String username, UserUpdateDTO userUpdateDTO) {
        Mockito.when(mockUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(mockUserRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO responseDTO = spyUserController.updateUserByUsername(username, userUpdateDTO);

        Mockito.verify(spyUserService).updateUserByUsername(username, userUpdateDTO);
        Mockito.verify(mockUserRepository).save(user);

        return responseDTO;
    }

}
