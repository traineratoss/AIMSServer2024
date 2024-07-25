package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.repository.*;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.implementation.SendEmailServiceImpl;
import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class SendEmailTest {
    
    SendEmailService spySendEmailService;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    AvatarRepository mockAvatarRepository;

    @Mock
    SubscriptionRepository mockSubscriptionRepository;

    @Mock
    IdeaRepository mockIdeaRepository;

    JavaMailSenderImpl spyJavaMailSender;

    Configuration spyConfiguration;

    String username = "username";
    String email = "mariusmurariu42@gmail.com";

    User user;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("ap6548088@gmail.com");
        javaMailSender.setPassword("wumhvwzmycduccma");

        // Set additional mail properties
        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setDirectoryForTemplateLoading(new File(".\\src\\main\\resources\\templates"));

        spyConfiguration = spy(configuration);

        spyJavaMailSender = spy(javaMailSender);

        spySendEmailService = spy(new SendEmailServiceImpl(
                mockUserRepository,
                mockAvatarRepository,
                spyJavaMailSender,
                spyConfiguration,
                mockSubscriptionRepository,
                mockIdeaRepository
        ));

        user = new User();
        user.setUsername(username);
        user.setEmail(email);
    }

    @Test
    public void testSendMailToUser() throws Exception {
        Mockito.when(mockUserRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        ReflectionTestUtils.setField(spySendEmailService, "adminEmail", email);

        spySendEmailService.sendEmailToUser(username);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.verify(spyJavaMailSender).send(messageCaptor.capture());

        MimeMessage message = messageCaptor.getValue();

        assertEquals("Login Request Received", message.getSubject());
    }

    @Test
    public void testSendMailToAdmin() throws Exception {

        Mockito.when(mockUserRepository.findUserByRole(any(Role.class))).thenReturn(List.of(user));
        Mockito.when(mockUserRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        ReflectionTestUtils.setField(spySendEmailService, "adminEmail", email);

        spySendEmailService.sendEmailToAdmins(username);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.verify(spyJavaMailSender).send(messageCaptor.capture());

        MimeMessage message = messageCaptor.getValue();

        assertEquals("Alert: Login Request Received", message.getSubject());
    }
}
