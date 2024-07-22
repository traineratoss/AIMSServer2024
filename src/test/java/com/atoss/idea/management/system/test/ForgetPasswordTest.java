package com.atoss.idea.management.system.test;

import com.atoss.idea.management.system.exception.UserAlreadyDeactivatedException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import com.atoss.idea.management.system.service.implementation.SendEmailServiceImpl;
import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import freemarker.template.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class ForgetPasswordTest {
    UserService spyUserService;

    @Spy
    SendEmailService spySendEmailService;

    @Mock
    UserRepository mockUserRepository;

    @Spy
    ModelMapper spyModelMapper;

    @Mock
    AvatarRepository mockAvatarRepository;

    @Mock
    PasswordEncoder mockPasswordEncoder;

    @Spy
    JavaMailSender mockEmailSender;

    @Spy
    Configuration mockConfiguration;

    String bcryptSalt;

    private User user;
    private String username = "username";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        spySendEmailService = spy(
                new SendEmailServiceImpl(
                        mockUserRepository,
                        mockAvatarRepository,
                        mockEmailSender,
                        mockConfiguration
                )
        );

        spyUserService = spy(
                new UserServiceImpl(
                        mockUserRepository,
                        spyModelMapper,
                        spySendEmailService,
                        mockAvatarRepository,
                        mockPasswordEncoder
                )
        );
        user = new User();
        user.setUsername(username);
        bcryptSalt = "$2a$10$QkRidA35ea0Fzm/ObrOEgO";
    }

    @Test
    void testSendForgetPasswordWithActiveUser() {
        user.setIsActive(true);

        Mockito.when(mockUserRepository.findByUsernameOrEmail(any(String.class), any(String.class)))
                .thenReturn(Optional.of(user));
        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.of(user));

        ReflectionTestUtils.setField(spySendEmailService, "bcryptSalt", bcryptSalt);

        ResponseEntity<Object> response = spyUserService.sendForgotPassword(username);

        Mockito.verify(spySendEmailService).sendEmailForgotPassword(any(String.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendForgetPasswordWithDeactivatedUserWithPassword() {
        user.setIsActive(false);
        user.setHasPassword(true);

        Mockito.when(mockUserRepository.findByUsernameOrEmail(any(String.class), any(String.class)))
                .thenReturn(Optional.of(user));

        assertThrows(UserAlreadyDeactivatedException.class, () -> spyUserService.sendForgotPassword(username));
    }

    @Test
    void testSendForgetPasswordWithDeactivatedUserWithoutPassword() {
        user.setIsActive(false);
        user.setHasPassword(false);

        Mockito.when(mockUserRepository.findByUsernameOrEmail(any(String.class), any(String.class)))
                .thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class, () -> spyUserService.sendForgotPassword(username));
    }
}
