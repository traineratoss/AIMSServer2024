package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.repository.entity.User;

import java.util.List;

public interface SendEmailService {
    /**
     * Sends an approval email to a user for account activation and performs necessary actions upon approval.
     *
     * This method generates a random password, sends an email to the user with activation details,
     * and updates the user's information in the system upon successful approval.
     *
     * @param username The username of the user to send the approval email to.
     * @throws AvatarNotFoundException If the default avatar with ID 1 is not found in the avatar repository.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendApproveEmailToUser(String username);

    /**
     /**
     * Sends a notification email to a user indicating that their registration request has been declined.
     *
     * This method sends an email to the user, notifying them that their registration request
     * has been rejected by the system administrators.
     *
     * @param username The username of the user to send the decline email to.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendDeclineEmailToUser(String username);

    /**
     * Sends a notification email to a user regarding a received login request.
     *
     * This method sends an email to the user to inform them that a login request
     * has been received for their account.
     *
     * @param username The username of the user to send the email notification to.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendEmailToUser(String username);

    /**
     * Sends a notification email to administrators regarding a received login request from a user.
     *
     * This method sends an email to each administrator in the system, informing them that a login request
     * has been received from a specific user.
     *
     * @param username The username of the user who initiated the login request.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendEmailToAdmins(String username);

    /**
     /**
     * Sends a password reset email to a user and updates their password upon request.
     *
     * @param username The username of the user for whom the password reset email will be sent.
     * @param otp The one-time password to be sent.
     *
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendEmailForgotPassword(String username, String otp);

    /**
     * Sends an account deactivation email to a user and updates their account status.
     *
     * This method sends an email to the user notifying them of their account deactivation
     * and updates their account status in the system.
     *
     * @param username The username of the user whose account is being deactivated.
     * @return `true` if the deactivation email is sent and the account status is updated successfully; otherwise, `false`.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    boolean sendDeactivateEmailToUser(String username);

    /**
     * Sends an account reactivation email to a user and updates their account status.
     *
     * This method sends an email to the user notifying them of their account reactivation,
     * generates and sets a new password for their account, and updates their account status in the system.
     *
     * @param username The username of the user to whom the reactivation email will be sent.
     * @return True if the reactivation email is sent and the user's account is successfully reactivated; otherwise, false.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    boolean sendActivateEmailToUser(String username);

    void sendEmailRatingChanged(String username, Long ideaId);

    void sendEmailChangedIdeaTitle(List<User> usernames, Long ideaId);

    void sendEmailChangedIdeaText(List<User> usernames, Long ideaId);
}
