package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.exception.UserAlreadyDeactivatedException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

public class LoginTest {

    UserService spyUserService;

    @Mock
    SendEmailService mockSendEmailService;

    @Mock
    UserRepository mockUserRepository;

    @Spy
    ModelMapper spyModelMapper;

    @Mock
    AvatarRepository mockAvatarRepository;

    private String userNameOrEmail;
    private String password = "$2a$10$QkRidA35ea0Fzm/ObrOEgO";

    User user;

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
        user = new User();
    }

    @Test
    void testLoginWithActiveUser() {
        user.setIsActive(true);
        user.setPassword(password);

        Mockito.when(mockUserRepository.findByUsernameOrEmail(userNameOrEmail, userNameOrEmail))
                .thenReturn(Optional.of(user));

        UserSecurityDTO responseDTO = spyUserService.login(userNameOrEmail, password);

        assertNotNull(responseDTO);
    }

    @Test
    void testLoginWithDeactivatedUser() {
        user.setIsActive(false);
        user.setPassword(password);

        Mockito.when(mockUserRepository.findByUsernameOrEmail(userNameOrEmail, userNameOrEmail))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(UserAlreadyDeactivatedException.class, () -> spyUserService.login(userNameOrEmail, password));
    }

    @Test
    void testLoginWithBadCredentials() {
        user.setPassword("$2a$10$QkRidA35ea0Fzm/ObrOEg1");

        assertThrows(BadCredentialsException.class, () -> spyUserService.login(userNameOrEmail, password));
    }
}
