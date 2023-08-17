package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final UserRepository userRepository;

    private final AvatarRepository avatarRepository;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;
    private final String companyName = "Company Name ";

    /**
     * Constructs a new instance of the SendEmailServiceImpl.
     *
     * This constructor initializes the SendEmailServiceImpl with the required dependencies, including
     * the UserRepository for accessing user data, the AvatarRepository for accessing avatar data, and
     * the JavaMailSender for sending emails.
     *
     * @param userRepository  The UserRepository instance for accessing user data.
     * @param avatarRepository The AvatarRepository instance for accessing avatar data.
     * @param emailSender      The JavaMailSender instance for sending emails.
     */
    public SendEmailServiceImpl(UserRepository userRepository, AvatarRepository avatarRepository, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.emailSender = emailSender;
    }

    @Override
    public void sendApproveEmailToUser(String username) {
        User user = getUserByUsername(username);
        String password = PasswordGenerator.generatePassayPassword(15);
        String emailTo = user.getEmail();
        String subject = "Account Activation for [Company Name] App";
        String text = getEmailTextForActivation(username, password, emailTo);
        sendEmail(emailTo, subject, text);
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
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        String subject = "Registration Request - Rejected";
        String text = getEmailTextForRegistrationRejected(username);
        sendEmail(emailTo, subject, text);
    }

    @Override
    public boolean sendDeactivateEmailToUser(String username) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        String subject = "Account Deactivation Notice";
        String text = getEmailTextForAccountDeactivation(username);
        sendEmail(emailTo, subject, text);
        // Set the isActive to false
        user.setIsActive(false);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean sendActivateEmailToUser(String username) {
        User user = getUserByUsername(username);
        String password = PasswordGenerator.generatePassayPassword(15);
        String emailTo = user.getEmail();
        String subject = "Account Reactivation Notice - Welcome Back!";
        String text = getEmailTextForAccountReactivation(username, password, emailTo);
        sendEmail(emailTo, subject, text);
        // Set the isActive to true and change password
        user.setIsActive(true);
        user.setPassword(BCrypt.hashpw(password, bcryptSalt));
        user.setIsFirstLogin(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public void sendEmailToUser(String username) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        String subject = "Alert: Login Request Received";
        //        String textAdmin = getEmailTextForLoginRequest(username, emailTo);
        String textUser = getEmailTextForLoginRequestForUser(username, emailTo);
        //        sendToAllAdmin(subject, textAdmin);
        sendEmail(emailTo, subject, textUser);
    }

    @Override
    public void sendEmailToAdmins(String username) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        String subject = "Alert: Login Request Received";
        String textAdmin = getEmailTextForLoginRequest(username, emailTo);
        sendToAllAdmin(subject, textAdmin);
    }

    @Override
    public void sendEmailForgotPassword(String username) {
        User user = getUserByUsername(username);
        String emailTo = user.getEmail();
        String subject = "Password Reset Request";
        String password = PasswordGenerator.generatePassayPassword(15);
        String text = getEmailTextForPasswordReset(username, password);
        sendEmail(emailTo, subject, text);
        //Change password
        user.setPassword(BCrypt.hashpw(password, bcryptSalt));
        user.setIsFirstLogin(true);
        userRepository.save(user);
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
     * Sends an email to all administrators with the provided subject and text.
     *
     * This method fetches a list of users with the role "ADMIN" from the user repository. It then iterates through
     * the list and sends an email to each administrator with the given subject and text content using the `sendEmail()`
     * method.
     *
     * @param subject The subject of the email to be sent to all administrators.
     * @param text    The text content of the email to be sent to all administrators.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    private void sendToAllAdmin(String subject, String text) {
        List<User> adminList = userRepository.findUserByRole(Role.ADMIN);
        adminList.forEach(admin -> sendEmail(admin.getEmail(), subject, text));
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
            helper.setText(text);
            emailSender.send(message);
            System.out.println("Email sent successfully to: " + emailTo);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates the email content for account activation.
     *
     * This method creates an email text welcoming the user to the application and providing the necessary information
     * for account activation. It includes the username, the generated password, and the company name in the email.
     *
     * @param username The username of the user to be addressed in the email.
     * @param password The generated password for the user's account activation.
     * @param emailTo  The email address of the user to whom the email will be sent.
     * @return The email content for account activation.
     */
    private String getEmailTextForActivation(String username, String password, String emailTo) {
        return "Dear "
                + username
                + "\nWelcome to "
                + companyName
                + "\nApp! We are thrilled to have you as a new member of our community."
                + "\nTo get started, you will need to activate your account. Below are your login credentials:"
                + "\nEmail: "
                + emailTo
                + "\nPassword: "
                + password
                + "\nBest regards,\nThe "
                + companyName
                + "Team";
    }

    /**
     * Generates the email content for a registration rejection.
     *
     * This method creates an email text notifying the user that their registration request has been rejected. It includes
     * the username and the company name in the email.
     *
     * @param username The username of the user to be addressed in the email.
     * @return The email content for registration rejection.
     */
    private String getEmailTextForRegistrationRejected(String username) {
        return "Dear "
                + username
                + "\nThank you for your interest and for submitting your registration request to  "
                + companyName
                + "\nWe regret to inform you that your registration request has been rejected for specific reasons. "
                + "We understand that this response may be disappointing, but please rest assured that the evaluation of"
                + " your application was conducted carefully and in accordance with our internal policies and regulations."
                + "\nThank you for your understanding."
                + "\nBest regards,\nThe "
                + companyName
                + "Team";
    }

    /**
     * Generates the email content for account deactivation.
     *
     * This method creates an email text informing the user that their account has been deactivated. It includes the username,
     * the company name, and the deactivation date in the email.
     *
     * @param username The username of the user to be addressed in the email.
     * @return The email content for account deactivation.
     */
    private String getEmailTextForAccountDeactivation(String username) {
        Date date = new Date();
        return "Dear "
                + username
                + "\nWe hope this message finds you well. We are writing to inform you that your account with "
                + companyName
                + " has been deactivated, effective as of. "
                + formatter.format(date)
                + "\nThe deactivation of your account is a result of your departure from "
                + companyName
                + ".As per our internal policies, user accounts are deactivated when an employee leaves the "
                + "company to ensure the security and integrity of our platform."
                + "\nWith the deactivation of your account, you will no longer have access to your account "
                + "and its associated features on our platform. "
                + "Any data or content associated with your account will be securely retained for a specific "
                + "period in accordance with our data retention policies."
                + "\nBest regards,"
                + companyName;
    }

    /**
     * Generates the email content for account reactivation.
     *
     * This method creates an email text informing the user that their account has been reactivated. It includes the username,
     * the company name, the reactivation date, and the newly generated password (if applicable) in the email.
     *
     * @param username The username of the user to be addressed in the email.
     * @param password The newly generated password for the user's account reactivation.
     * @param emailTo  The email address of the user to whom the email will be sent.
     * @return The email content for account reactivation.
     */
    private String getEmailTextForAccountReactivation(String username, String password, String emailTo) {
        Date date = new Date();
        return "Dear "
                + username
                + "\nWe hope this message finds you well. We are delighted to inform you that your account with "
                + companyName
                + " has been reactivated, effective immediately. "
                + formatter.format(date)
                + "\nYour account was previously deactivated due to your departure from "
                + companyName
                + ".However, we are thrilled to welcome you back as you have returned to our company. "
                + "company to ensure the security and integrity of our platform."
                + "\nWith the reactivation of your account, you will once again have full access to all the features "
                + "and services offered on our platform. Your data and previous interactions have been securely restored,"
                + " ensuring a seamless experience as you resume using our platform. "
                + "Below are your login credentials:"
                + "\nEmail: "
                + emailTo
                + "\nPassword: "
                + password
                + "\nShould you encounter any issues or have any questions regarding your reactivated account, please "
                + "do not hesitate to reach out to our support team. We are here to assist you with anything you may need."
                + "\nWelcome back!"
                + "\nBest regards,"
                + companyName;
    }

    /**
     * Generates the email content for login request notification to administrators.
     *
     * This method creates an email text notifying the administrators about a login request from a user. It includes the
     * username, the user's email address, and the date and time of the login request in the email.
     *
     * @param username The username of the user who initiated the login request.
     * @param emailTo  The email address of the user who initiated the login request.
     * @return The email content for login request notification to administrators.
     */
    private String getEmailTextForLoginRequest(String username, String emailTo) {
        Date date = new Date();
        return  "Dear "
                + "Admin"
                + "\nI hope this message finds you well. I am writing to inform you that we have recorded a login request in our account system."
                + "\nRequest details:"
                + "\nUser: "
                + username
                + "\nEmail: "
                + emailTo
                + "\nData and time: "
                + formatter.format(date)
                + "\nIf you confirm the request, please provide us with your approval to proceed with sending the password.";
    }

    /**
     * Generates the email content for login request notification to the user.
     *
     * This method creates an email text notifying the user about their login request recorded in the system. It includes
     * the username, the user's email address, and the date and time of the login request in the email.
     *
     * @param username The username of the user who initiated the login request.
     * @param emailTo  The email address of the user who initiated the login request.
     * @return The email content for login request notification to the user.
     */
    private String getEmailTextForLoginRequestForUser(String username, String emailTo) {
        Date date = new Date();
        return  "Dear "
                + username
                + "\nI hope this message finds you well. I am writing to inform you that we have recorded a login request in our account system."
                + "\nRequest details:"
                + "\nUser: "
                + username
                + "\nEmail: "
                + emailTo
                + "\nData and time: "
                + formatter.format(date);
    }

    /**
     * Generates the email content for a password reset request.
     *
     * This method creates an email text providing instructions to the user for resetting their account password. It includes
     * the username, the company name, a link to the login page, the temporary password, and some guidelines for setting
     * a new password.
     *
     * @param username The username of the user who requested the password reset.
     * @param password The temporary password provided for the password reset.
     * @return The email content for the password reset request.
     */
    private String getEmailTextForPasswordReset(String username, String password) {
        String link = "http://127.0.0.1:5173/login";
        return "Dear "
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
    }
}
