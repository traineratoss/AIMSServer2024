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

    /**
     * Sends an email to notify the subscribed users of a certain idea that its rating average has changed
     *
     *
     * @param username The username of the user which is subscribed to the idea
     * @param ideaId the id of the idea whose rating average has changed
     */
    void sendEmailRatingChanged(String username, Long ideaId);


    /**
     * Sends an email to notify the subscribed users of a certain idea that its text has changed
     *
     *
     * @param usernames The usernames of the users which are subscribed to the idea
     * @param ideaId the id of the idea whose text has changed
     * @param oldText the text of the idea before it was updated
     * @param oldTitle the title of the idea before it was updated
     * @param oldDocs  the names of the documents attached to the idea before it was updated
     * @param newDocs   the names of the documents attached to the idea after it was updated
     */
    void sendEmailUpdatedIdea(List<User> usernames, Long ideaId, String oldText, String oldTitle, String oldDocs, String newDocs);

    /**
     * Sends an email to notify the subscribed users of a certain idea that its comment has changed
     *
     *
     * @param usernames The usernames of the users which are subscribed to the idea
     * @param commentId the id of the changed comment
     * @param comment The comment text that was added
     */
    void sendEmailDeletedComment(List<User> usernames, Long commentId, String comment);

    /**
     * Sends an email to notify the subscribed users of a certain idea that its comment has changed
     *
     *
     * @param fileName The usernames of the users which are subscribed to the idea
     * @param commentId the id of the changed comment
     * @param comment The comment text that was added
     * @param username a
     * @param subject a
     */
    void sendEmailChangedComment(String fileName, String username, String comment, Long commentId, String subject);

    /**
     * Sends an email to notify the subscribed users of a certain idea that its comment has changed
     *
     *
     * @param usernames The usernames of the users which are subscribed to the idea
     * @param commentId the id of the changed comment
     * @param comment The comment text that was added
     */
    void sendEmailAddedComment(List<User> usernames, Long commentId, String comment);
}
