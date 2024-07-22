package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.exception.ApproveAlreadyGrantedException;
import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.exception.UserAlreadyActivatedException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Avatar;
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

public class UserRegisterApprovalTest {
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
        bcryptSalt = "$2a$10$QkRidA35ea0Fzm/ObrOEgO";
    }

    @Test
    void testSendApproveEmailToUserWithoutPassword() {
        user.setHasPassword(false);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        Mockito.when(mockAvatarRepository.findById(any(Long.class))).thenReturn(Optional.of(new Avatar()));

        ReflectionTestUtils.setField(spySendEmailService, "bcryptSalt", bcryptSalt);

        ResponseEntity<Object> response = spyUserService.sendApproveEmail(username);

        Mockito.verify(spySendEmailService).sendApproveEmailToUser(any(String.class));
        Mockito.verify(mockUserRepository).save(any(User.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendApproveEmailToUserWithoutPasswordWhereAvatarNotFound() {
        user.setHasPassword(false);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        Mockito.when(mockAvatarRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ReflectionTestUtils.setField(spySendEmailService, "bcryptSalt", bcryptSalt);

        assertThrows(AvatarNotFoundException.class, () -> spyUserService.sendApproveEmail(username));
    }

    @Test
    void testSendApproveEmailToUserWithPassword() {
        user.setHasPassword(true);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(ApproveAlreadyGrantedException.class, () -> spyUserService.sendApproveEmail(username));
    }

    @Test
    void testSendDeclineEmailToUserWithPassword() {
        user.setHasPassword(true);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(ApproveAlreadyGrantedException.class, () -> spyUserService.sendDeclineEmail(username));
    }

    @Test
    void testSendDeclineEmailToUserWithoutPasswordWithActiveAccount() {
        user.setHasPassword(false);
        user.setIsActive(true);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));


        assertThrows(UserAlreadyActivatedException.class, () -> spyUserService.sendDeclineEmail(username));
        Mockito.verify(spySendEmailService).sendDeclineEmailToUser(any(String.class));
    }

    @Test
    void testSendDeclineEmailToUserWithoutPasswordWithDeactivatedAccount() {
        user.setHasPassword(false);
        user.setIsActive(false);

        Mockito.when(mockUserRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.ofNullable(user));


        ResponseEntity<Object> response = spyUserService.sendDeclineEmail(username);

        Mockito.verify(spySendEmailService).sendDeclineEmailToUser(any(String.class));
        Mockito.verify(mockUserRepository).delete(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
