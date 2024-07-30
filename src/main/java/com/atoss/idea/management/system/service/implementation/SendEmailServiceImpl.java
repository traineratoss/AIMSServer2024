package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.repository.*;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final IdeaRepository ideaRepository;
    private final JavaMailSender emailSender;

    private final Configuration configuration;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;
    private final String companyName = "Atoss Idea Management System";

    /**
     * Constructs a new instance of the SendEmailServiceImpl.
     *
     * This constructor initializes the SendEmailServiceImpl with the required dependencies, including
     * the UserRepository for accessing user data, the AvatarRepository for accessing avatar data, and
     * the JavaMailSender for sending emails.
     *
     * @param userRepository   The UserRepository instance for accessing user data.
     * @param avatarRepository The AvatarRepository instance for accessing avatar data.
     * @param emailSender      The JavaMailSender instance for sending emails.
     * @param configuration The Configuration instance for email templates.
     * @param ideaRepository The repository for the Idea Entity
     * @param subscriptionRepository The repository for the Subscription Entity
     */
    public SendEmailServiceImpl(UserRepository userRepository,
                                AvatarRepository avatarRepository,
                                JavaMailSender emailSender,
                                Configuration configuration,
                                SubscriptionRepository subscriptionRepository,
                                IdeaRepository ideaRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.emailSender = emailSender;
        this.configuration = configuration;
        this.subscriptionRepository = subscriptionRepository;
        this.ideaRepository = ideaRepository;
    }

    @Override
    public void sendApproveEmailToUser(String username) {
        User user = getUserByUsername(username);
        String password = PasswordGenerator.generatePassayPassword(15);
        String subject = "Account Activation for "  + companyName + " app";
        sendEmailUtils("welcome-template.ftl", username, password, subject);
        // Set the password, status, and isActive for the user
        user.setPassword(BCrypt.hashpw(password, bcryptSalt));
        user.setIsActive(true);
        user.setHasPassword(true);
        user.setRole(Role.STANDARD);
        user.setFullName("");
        Avatar avatar = avatarRepository.findById(1L).orElseThrow(() -> new AvatarNotFoundException("Avatar not found!"));
        user.setAvatar(avatar);
        userRepository.save(user);
    }

    @Override
    public void sendDeclineEmailToUser(String username) {
        sendEmailUtils("registration-reject-template.ftl", username, "", "Registration Request - Rejected");
    }


    /**
     * Sends an email to notify the subscribed users of a certain idea that its text has changed
     *
     *
     * @param usernames The usernames of the users which are subscribed to the idea
     * @param ideaId the id of the idea whose text has changed
     */
    public void sendEmailChangedIdeaText(List<User> usernames, Long ideaId) {
        Idea idea = ideaRepository.findById(ideaId).get();
        for (User user : usernames) {
            String username = user.getUsername();
            sendEmailIdeaSubscription("text-change-subscription-template.ftl", username, idea.getTitle(), idea.getText(), ideaId, "Idea text change");
        }
    }

    /**
     * Sends an email to notify the subscribed users of a certain idea that its title has changed
     *
     *
     * @param usernames The usernames of the users which are subscribed to the idea
     * @param ideaId the id of the idea whose title has changed
     */
    public void sendEmailChangedIdeaTitle(List<User> usernames, Long ideaId) {
        Idea idea = ideaRepository.findById(ideaId).get();
        for (User user : usernames) {
            String username = user.getUsername();
            sendEmailIdeaSubscription(
                    "title-change-subscription-template.ftl",
                    username,
                    idea.getTitle(),
                    idea.getText(),
                    ideaId,
                    "Idea title change"
            );
        }
    }

    @Override
    public boolean sendDeactivateEmailToUser(String username) {
        User user = getUserByUsername(username);
        sendEmailUtils("account-deactivation-template.ftl", username, "", "Account Deactivation Notice");
        // Set the isActive to false
        user.setIsActive(false);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean sendActivateEmailToUser(String username) {
        User user = getUserByUsername(username);
        String password = PasswordGenerator.generatePassayPassword(15);
        sendEmailUtils("account-reactivation-template.ftl", username, password, "Account Reactivation Notice - Welcome Back!");
        // Set the isActive to true and change password
        user.setIsActive(true);
        user.setPassword(BCrypt.hashpw(password, bcryptSalt));
        user.setIsFirstLogin(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public void sendEmailToUser(String username) {
        sendEmailUtils("login-request-user-template.ftl", username, "", "Login Request Received");
    }

    @Override
    public void sendEmailToAdmins(String username) {
        List<User> adminList = userRepository.findUserByRole(Role.ADMIN);
        adminList.forEach(admin -> sendEmailUtilsToAdmins("login-request-admin-template.ftl", username, admin, "Alert: Login Request Received"));
    }

    @Override
    public void sendEmailForgotPassword(String username, String otp) {
        sendEmailUtils("password-reset-template.ftl", username, otp, "Password Reset Request");
    }

    @Override
    public void sendEmailRatingChanged(String username, Long ideaId) {
        sendEmailRatingChangedService("star-rating-changes-template.ftl", username, "Rating changed", ideaId);
    }

    /**
     * Retrieves a user from the repository based on the provided username.
     *
     * This method fetches a user from the user repository using the given username. If the user does not exist,
     * a RuntimeException is thrown.
     *
     * @param username The username of the user to retrieve.
     * @return The User object corresponding to the given username.
     * @throws RuntimeException If the user does not exist in the repository.
     */
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User does not exist!"));
    }


    /**
     * Sends an email using email template utilities.
     *
     * This method generates and sends an email using the provided email template file, user information,
     * and subject. It retrieves user-specific details, processes the template, and sends the email.
     *
     * @param fileName The name of the email template file to use.
     * @param username The username of the user for whom the email is intended.
     * @param password The password to include in the email content.
     * @param subject The subject of the email.
     */
    private void sendEmailUtils(String fileName, String username, String password, String subject) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("username", username);
        mapUser.put("email", user.getEmail());
        mapUser.put("companyName", companyName);
        mapUser.put("password", password);
        mapUser.put("date", new Date().toString());
        mapUser.put("imageUrl", "./welcome.jpg");
        try {
            Template template  = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(emailTo, subject, htmlTemplate);
        } catch (IOException | TemplateException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void sendEmailRatingChangedService(String fileName, String username, String subject, Long ideaId) {
        User user = getUserByUsername(username);
        Idea idea = ideaRepository.findById(ideaId).orElseThrow(() -> new IdeaNotFoundException("Idea not found"));
        String emailTo = user.getEmail();
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("username", username);
        mapUser.put("email", user.getEmail());
        mapUser.put("companyName", companyName);
        mapUser.put("date", new Date().toString());
        mapUser.put("imageUrl", "./welcome.jpg");
        mapUser.put("ideaTitle", idea.getTitle());
        mapUser.put("newRating", idea.getRatingAvg().intValue());
        try {
            Template template = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(emailTo, subject, htmlTemplate);
        } catch (IOException | TemplateException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Sends an email to administrators using email template utilities.
     *
     * This method generates and sends an email to administrators using the provided email template file,
     * user information, admin details, and subject. It retrieves user-specific and admin-specific details,
     * processes the template, and sends the email to administrators.
     *
     * @param fileName The name of the email template file to use.
     * @param usernameUser The username of the user for whom the email is intended.
     * @param admin The User object representing the administrator to whom the email is sent.
     * @param subject The subject of the email.
     */
    private void sendEmailUtilsToAdmins(String fileName, String usernameUser, User admin, String subject) {
        User user = getUserByUsername(usernameUser);
        String emailTo = user.getEmail();
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("usernameUser", usernameUser);
        mapUser.put("emailUser", user.getEmail());
        mapUser.put("usernameAdmin", admin.getUsername());
        mapUser.put("companyName", companyName);
        mapUser.put("date", new Date().toString());
        try {
            Template template  = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(admin.getEmail(), subject, htmlTemplate);
        } catch (IOException | TemplateException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Sends an email to the provided recipient email address with the given subject and text content.
     *
     * This method uses the JavaMail API to send an email to the specified email address with the given subject and
     * text content. The email is sent using the emailSender object, and if any errors occur during the email sending
     * process, a RuntimeException is thrown.
     *
     * @param emailTo The recipient email address to send the email to.
     * @param subject The subject of the email.
     * @param text    The text content of the email.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    private void sendEmail(String emailTo, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(adminEmail);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(message);
            System.out.println("Email sent successfully to: " + emailTo);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmailIdeaSubscription(String fileName, String username, String title, String text, Long ideaId, String subject) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        Idea idea = getIdeaById(ideaId);

        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("username", username);
        mapUser.put("email", user.getEmail());
        mapUser.put("companyName", companyName);
        mapUser.put("newTitle", idea.getTitle());
        mapUser.put("newText", idea.getText());
        mapUser.put("imageUrl", "./welcome.jpg");
        try {
            Template template  = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(emailTo, subject, htmlTemplate);
        } catch (IOException | TemplateException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Retrieves an idea from the repository based on the provided id
     *
     * This method fetches an idea from the idea repository using the given id. If the idea does not exist,
     * a RuntimeException is thrown.
     *
     * @param ideaId The id of the idea to retrieve.
     * @return The Idea object corresponding to the given id.
     * @throws RuntimeException If the user does not exist in the repository.
     */
    private Idea getIdeaById(Long ideaId) {
        return ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea does not exist!"));
    }



}
