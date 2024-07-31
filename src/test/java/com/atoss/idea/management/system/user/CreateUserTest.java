package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.controller.UserController;
import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class CreateUserTest {
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


    private String username = "username";
    private String email = "email@email.email";

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
    }

    @Test
    void testAddUser() {



        Mockito.when(mockUserRepository.findByUsernameOrEmail(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        UserResponseDTO responseDTO = spyUserController.addUser(username, email).getBody();

        assertNotNull(responseDTO);
        assertEquals(username, responseDTO.getUsername());
        assertEquals(email, responseDTO.getEmail());
        assertNull(responseDTO.getRole());
        assertNull(responseDTO.getAvatar());
        assertNull(responseDTO.getFullName());
        assertNull(responseDTO.getIdeas());
    }

    @Test
    void testAddUserWithExistingUsername() {
        String username = "username";
        String email = "email@email.email";

        Mockito.when(mockUserRepository.findByUsernameOrEmail(any(String.class), any(String.class)))
                .thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> spyUserController.addUser(username, email));
    }
}
