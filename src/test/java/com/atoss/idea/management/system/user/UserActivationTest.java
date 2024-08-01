package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.exception.UserAlreadyActivatedException;
import com.atoss.idea.management.system.exception.UserAlreadyDeactivatedException;
import com.atoss.idea.management.system.repository.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class UserActivationTest {
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
    SubscriptionRepository mockSubscriptionRepository;

    @Mock
    IdeaRepository mockIdeaRepository;

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
                        mockConfiguration,
                        mockSubscriptionRepository,
                        mockIdeaRepository
                )
        );

        spyUserService = spy(
                new UserServiceImpl(
                        mockUserRepository,
                        spyModelMapper,
                        spySendEmailService,
                        mockAvatarRepository
                )
        );
        user = new User();
        bcryptSalt = "$2a$10$QkRidA35ea0Fzm/ObrOEgO";
    }

    @Test
    void testSendDeactivationEmailToActiveUser() {
        user.setIsActive(true);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        ResponseEntity<Object> response = spyUserService.sendDeactivateEmail(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendDeactivationEmailToDeactivatedUser() {
        user.setIsActive(false);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(UserAlreadyDeactivatedException.class, () -> spyUserService.sendDeactivateEmail(username));
    }

    @Test
    void testSendActivationEmailToDeactivatedUser() {
        user.setIsActive(false);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        ReflectionTestUtils.setField(spySendEmailService, "bcryptSalt", bcryptSalt);

        ResponseEntity<Object> response = spyUserService.sendActivateEmail(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendActivationEmailToActivatedUser() {
        user.setIsActive(true);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(UserAlreadyActivatedException.class, () -> spyUserService.sendActivateEmail(username));
    }
}
