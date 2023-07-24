package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import com.google.common.hash.Hashing;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    private final UserRepository userRepository;

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public SendEmailServiceImpl(UserRepository userRepository, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmailToUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User dose not exist!"));
        String password = PasswordGenerator.generatePassayPassword(15);
        String hashPassword = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        String emailTo = user.getEmail();
        String subject = "Account Activation for [Company Name] App";
        String text = "Dear "
                + username
                + "\nWelcome to "
                + "[Company Name] "
                + "\nApp! We are thrilled to have you as a new member of our community."
                + "\nTo get started, you will need to activate your account. Below are your login credentials:"
                + "\nEmail: "
                + emailTo
                + "\nPassword: "
                + password
                + "\nBest regards,\nThe "
                + "[Company Name] "
                + "Team";
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(adminEmail);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(text);
            emailSender.send(message);
            //Set the password, status and isActive for user
            user.setPassword(hashPassword);
            user.setIsActive(true);
            user.setRole(Role.STANDARD);
            userRepository.save(user);
            System.out.println("Email sent successfully to: " + emailTo);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailToAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User dose not exist!"));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String subject = "Alert: Login Request Received";
        String text = "Dear "
                + username
                + "\nI hope this message finds you well. I am writing to inform you that we have recorded a login request in our account system."
                + "\nRequest details:"
                + "\nUser: "
                + user.getUsername()
                + "\nEmail: "
                + user.getEmail()
                + "\nData and time: "
                + formatter.format(date)
                + "\nIf you confirm the request, please provide us with your approval to proceed with sending the password.";
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(adminEmail);
            helper.setTo(adminEmail);
            helper.setSubject(subject);
            helper.setText(text);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailForgotPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User dose not exist!"));
        String link = "http://127.0.0.1:5173/login";
        String companyName = "Company Name";
        String password = PasswordGenerator.generatePassayPassword(15);
        String hashPassword = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        String emailTo = user.getEmail();
        String subject = "Password Reset Request";
        String text = "Dear "
                + username
                + "\nWelcome to "
                + companyName
                + "\n\nWe hope you are doing well. It appears that you have requested a password reset for your account.  "
                + "We understand how important it is to regain access to your account, and we're here to assist you with the process."
                + "\n\nTo proceed with the password reset, please follow the instructions below:"
                + "\n1.Click on the following link to access the login page: "
                + link
                + "\n2.Once you are on the login page, use the temporary password provided below to log in to your account: "
                + "\n\tTemporary Password: "
                + password
                + "\n3.After successfully logging in, you will be automatically redirected to the password reset page."
                + "\n4.On the password reset page, choose a new, strong, and unique password for your account. "
                + "Please remember to keep your password secure and avoid sharing it with anyone. "
                + "\n\nBest regards,\nThe "
                + companyName
                + "Team";
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(adminEmail);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(text);
            emailSender.send(message);
            //Set the password
            user.setPassword(hashPassword);
            userRepository.save(user);
            System.out.println("Email sent successfully to: " + emailTo);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
