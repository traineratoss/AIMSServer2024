package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.repository.*;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Log4j2
public class SendEmailServiceImpl implements SendEmailService {

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final IdeaRepository ideaRepository;
    private final JavaMailSender emailSender;

    private final Configuration configuration;

    private final CommentRepository commentRepository;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;
    private final String companyName = "Atoss Idea Management System";

    /**
     * Constructs a new instance of the SendEmailServiceImpl.
     * <p>
     * This constructor initializes the SendEmailServiceImpl with the required dependencies, including
     * the UserRepository for accessing user data, the AvatarRepository for accessing avatar data, and
     * the JavaMailSender for sending emails.
     *
     * @param userRepository         The UserRepository instance for accessing user data.
     * @param avatarRepository       The AvatarRepository instance for accessing avatar data.
     * @param emailSender            The JavaMailSender instance for sending emails.
     * @param configuration          The Configuration instance for email templates.
     * @param ideaRepository         The repository for the Idea Entity
     * @param subscriptionRepository The repository for the Subscription Entity
     * @param commentRepository      The commentRepository instance for accessing comment data.
     */
    public SendEmailServiceImpl(UserRepository userRepository,
                                AvatarRepository avatarRepository,
                                JavaMailSender emailSender,
                                Configuration configuration,
                                SubscriptionRepository subscriptionRepository,
                                IdeaRepository ideaRepository,
                                CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.emailSender = emailSender;
        this.configuration = configuration;
        this.subscriptionRepository = subscriptionRepository;
        this.ideaRepository = ideaRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void sendApproveEmailToUser(String username) {
        if (log.isInfoEnabled()) {
            log.info("Generated approval email for user with username: {}", username);
        }

        User user = getUserByUsername(username);
        String password = PasswordGenerator.generatePassayPassword(15);
        String subject = "Account Activation for " + companyName + " app";
        sendEmailUtils("welcome-template.ftl", username, password, subject);
        if (log.isInfoEnabled()) {
            log.info("Approval email sent to user with username: {}", username);
        }
        // Set the password, status, and isActive for the user
        user.setPassword(BCrypt.hashpw(password, bcryptSalt));
        user.setIsActive(true);
        user.setHasPassword(true);
        user.setRole(Role.STANDARD);
        user.setFullName("");
        Avatar avatar = avatarRepository.findById(1L).orElseThrow(() -> new AvatarNotFoundException("Avatar not found!"));
        user.setAvatar(avatar);
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("User with username: {} has been approved successfully", username);
        }
    }

    @Override
    public void sendDeclineEmailToUser(String username) {
        sendEmailUtils("registration-reject-template.ftl", username, "", "Registration Request - Rejected");
        if (log.isInfoEnabled()) {
            log.info("Decline email successfully sent to user with username: {}", username);
        }
    }


    /**
     * Sends an email to notify the subscribed users of a certain idea that it has been updated
     *
     * @param usernames The usernames of the users which are subscribed to the idea
     * @param ideaId    the id of the idea whose text has changed
     * @param oldText   the text of the idea before it was updated
     * @param oldTitle  the title of the idea before it was updated
     * @param oldDocs   the names of the documents attached to the idea before it was updated
     * @param newDocs   the names of the documents attached to the idea after it was updated
     */
    public void sendEmailUpdatedIdea(List<User> usernames, Long ideaId, String oldText, String oldTitle, String oldDocs, String newDocs) {
        Idea idea = ideaRepository.findById(ideaId).get();
        if (log.isInfoEnabled()) {
            log.info("Retrieved idea with id: {} for updating subscribers", idea.getTitle());
        }
        for (User user : usernames) {
            String username = user.getUsername();

            sendEmailIdeaSubscription("text-change-subscription-template.ftl",
                    username,
                    oldTitle,
                    oldText,
                    oldDocs,
                    newDocs,
                    ideaId,
                    "AIMS Updated Idea");
            if (log.isInfoEnabled()) {
                log.info("Successfully sent update idea email to user with username: {}", username);
            }

        }
    }

    @Override
    public boolean sendDeactivateEmailToUser(String username) {
        User user = getUserByUsername(username);
        sendEmailUtils("account-deactivation-template.ftl", username, "", "Account Deactivation Notice");
        // Set the isActive to false
        user.setIsActive(false);
        userRepository.save(user);
        if (log.isInfoEnabled()) {
            log.info("User with username: {} has been deactivated successfully", username);
        }
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

        if (log.isInfoEnabled()) {
            log.info("User account activated successfully for username: {}", username);
        }

        return true;
    }

    @Override
    public void sendEmailToUser(String username) {
        sendEmailUtils("login-request-user-template.ftl", username, "", "Login Request Received");
        if (log.isInfoEnabled()) {
            log.info("Login request email sent successfully to user: {}", username);
        }
    }

    @Override
    public void sendEmailToAdmins(String username) {
        List<User> adminList = userRepository.findUserByRole(Role.ADMIN);
        if (log.isInfoEnabled()) {
            log.info("Found {} admins to notify about the login request for user: {}", adminList.size(), username);
        }
        adminList.forEach(admin -> sendEmailUtilsToAdmins("login-request-admin-template.ftl", username, admin, "Alert: Login Request Received"));
        if (log.isInfoEnabled()) {
            log.info("Sent login request alert to admins");
        }
    }

    @Override
    public void sendEmailForgotPassword(String username, String otp) {
        sendEmailUtils("password-reset-template.ftl", username, otp, "Password Reset Request");
        if (log.isInfoEnabled()) {
            log.info("Password reset email sent successfully to user: {}", username);
        }
    }

    @Override
    public void sendEmailRatingChanged(String username, Long ideaId) {
        sendEmailRatingChangedService("star-rating-changes-template.ftl", username, "Rating changed", ideaId);
        if (log.isInfoEnabled()) {
            log.info("Rating change notification email sent successfully to user: {}", username);
        }
    }

    /**
     * Retrieves a user from the repository based on the provided username.
     * <p>
     * This method fetches a user from the user repository using the given username. If the user does not exist,
     * a RuntimeException is thrown.
     *
     * @param username The username of the user to retrieve.
     * @return The User object corresponding to the given username.
     * @throws RuntimeException If the user does not exist in the repository.
     */
    private User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new RuntimeException("User does not exist!");
        });

        if (log.isInfoEnabled()) {
            log.info("User successfully retrieved with username: {}", username);
        }

        return user;
    }


    /**
     * Sends an email using email template utilities.
     * <p>
     * This method generates and sends an email using the provided email template file, user information,
     * and subject. It retrieves user-specific details, processes the template, and sends the email.
     *
     * @param fileName The name of the email template file to use.
     * @param username The username of the user for whom the email is intended.
     * @param password The password to include in the email content.
     * @param subject  The subject of the email.
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
            Template template = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(emailTo, subject, htmlTemplate);

            if (log.isInfoEnabled()) {
                log.info("Email sent successfully to: {} with subject: {}", emailTo, subject);
            }

        } catch (IOException | TemplateException exception) {
            if (log.isErrorEnabled()) {
                log.error("Failed to send email to: {} error: {}", emailTo, exception.getMessage());
            }
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
        BigDecimal rating = new BigDecimal(idea.getRatingAvg().floatValue()).setScale(2, RoundingMode.HALF_UP);
        mapUser.put("newRating", rating.floatValue());
        try {
            Template template = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(emailTo, subject, htmlTemplate);

            if (log.isInfoEnabled()) {
                log.info("Email sent successfully to: {}", emailTo);
            }

        } catch (IOException | TemplateException exception) {
            if (log.isErrorEnabled()) {
                log.error("Failed to send email to: {} error: {}", emailTo, exception.getMessage());
            }
        }
    }

    /**
     * Sends an email to administrators using email template utilities.
     * <p>
     * This method generates and sends an email to administrators using the provided email template file,
     * user information, admin details, and subject. It retrieves user-specific and admin-specific details,
     * processes the template, and sends the email to administrators.
     *
     * @param fileName     The name of the email template file to use.
     * @param usernameUser The username of the user for whom the email is intended.
     * @param admin        The User object representing the administrator to whom the email is sent.
     * @param subject      The subject of the email.
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
            Template template = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(admin.getEmail(), subject, htmlTemplate);
            if (log.isInfoEnabled()) {
                log.info("Email sent successfully to admin");
            }
        } catch (IOException | TemplateException exception) {
            if (log.isErrorEnabled()) {
                log.error("Failed to send email to admin, error: {}", exception.getMessage());
            }

        }
    }

    /**
     * Sends an email to the provided recipient email address with the given subject and text content.
     * <p>
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
            if (log.isInfoEnabled()) {
                log.info("Email sent successfully to: {}", emailTo);
            }
            System.out.println("Email sent successfully to: " + emailTo);
        } catch (MessagingException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to send email to: {} error: {}", emailTo, e.getMessage());
            }

            throw new RuntimeException(e);
        }
    }

    private void sendEmailIdeaSubscription(String fileName,
                                           String username,
                                           String oldTitle,
                                           String oldText,
                                           String oldDocs,
                                           String newDocs,
                                           Long ideaId,
                                           String subject) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        Idea idea = getIdeaById(ideaId);

        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("username", username);
        mapUser.put("email", user.getEmail());
        mapUser.put("companyName", companyName);
        mapUser.put("newTitle", idea.getTitle());
        mapUser.put("newText", idea.getText());
        mapUser.put("oldText", oldText);
        mapUser.put("oldTitle", oldTitle);
        if (oldDocs.isEmpty()) {
            mapUser.put("oldDocs", "No documents were attached");
        } else {
            mapUser.put("oldDocs", oldDocs);
        }

        if (newDocs.isEmpty()) {
            mapUser.put("newDocs", "No documents were attached");
        } else {
            mapUser.put("newDocs", newDocs);
        }

        mapUser.put("imageUrl", "./welcome.jpg");

        try {
            Template template = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapUser);
            sendEmail(emailTo, subject, htmlTemplate);

            if (log.isInfoEnabled()) {
                log.info("Idea subscription email sent successfully to: {}", emailTo);
            }

        } catch (IOException | TemplateException exception) {
            if (log.isErrorEnabled()) {
                log.error("Failed to send idea subscription email to: {} error: {}", username, exception.getMessage());
            }
        }
    }

    /**
     * Retrieves an idea from the repository based on the provided id
     * <p>
     * This method fetches an idea from the idea repository using the given id. If the idea does not exist,
     * a RuntimeException is thrown.
     *
     * @param ideaId The id of the idea to retrieve.
     * @return The Idea object corresponding to the given id.
     * @throws RuntimeException If the user does not exist in the repository.
     */
    private Idea getIdeaById(Long ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Idea with id: {} does not exist!", ideaId);
                    }
                    return new IdeaNotFoundException("Idea does not exist!");
                });
        if (log.isInfoEnabled()) {
            log.info("Idea with title: {} was retrieved successfully.", idea.getTitle());
        }
        return idea;
    }

    /**
     * Retrieves a comment from the repository based on the provided id
     * This method fetches a idea from the comment repository using the given id. If the comment does not exist,
     *
     * @param commentId The id of the idea to retrieve.
     * @param comment   The comment text
     * @throws RuntimeException If the user does not exist in the repository.
     */
    @Override
    public void sendEmailDeletedComment(List<User> usernames, Long commentId, String comment) {
        for (User user : usernames) {
            if (log.isDebugEnabled()) {
                log.debug("Sending deleted comment notification to user: {}", user.getUsername());
            }
            sendEmailChangedComment("comment-delete-subscription-template.ftl",
                    user.getUsername(),
                    comment,
                    commentId,
                    "Deleted comment on subscribed idea.");
        }
    }

    /**
     * Retrieves a comment from the repository based on the provided id
     * This method fetches a comment from the comment repository using the given id. If the comment does not exist,
     *
     * @param commentId The id of the comment to retrieve.
     * @param usernames the usernames of the users who are subscribed to the idea
     * @param comment   the text of the comment that has been added
     */
    @Override
    public void sendEmailAddedComment(List<User> usernames, Long commentId, String comment) {
        for (User user : usernames) {
            if (log.isDebugEnabled()) {
                log.debug("Sending added comment notification to user: {}", user.getUsername());
            }
            sendEmailChangedComment("comment-added-subscription-template.ftl",
                    user.getUsername(),
                    comment,
                    commentId,
                    "Added comment on subscribed idea.");
        }
    }

    private Comment getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Comment with id: {} does not exist!", commentId);
                    }
                    return new CommentNotFoundException();
                });

        if (log.isInfoEnabled()) {
            log.info("Comment with text: {} was found successfully.", comment.getCommentText());
        }
        return comment;
    }

    @Override
    public void sendEmailChangedComment(String fileName, String username, String commentText, Long commentId, String subject) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        Comment comment = getCommentById(commentId);

        Map<String, Object> mapComment = new HashMap<>();
        mapComment.put("username", username);
        mapComment.put("email", user.getEmail());
        mapComment.put("companyName", companyName);
        mapComment.put("newComment", comment.getCommentText());
        mapComment.put("imageUrl", "./welcome.jpg");
        try {
            Template template = configuration.getTemplate(fileName);
            String htmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, mapComment);
            sendEmail(emailTo, subject, htmlTemplate);
            if (log.isInfoEnabled()) {
                log.info("Email sent successfully to {} regarding the change in comment with text: {}", emailTo, comment.getCommentText());
            }
        } catch (IOException | TemplateException exception) {
            if (log.isErrorEnabled()) {
                log.error("Failed to send email to {} regarding the change in comment, error: {}", emailTo, exception.getMessage());
            }
        }
    }
}