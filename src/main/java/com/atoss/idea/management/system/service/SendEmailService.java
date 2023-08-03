package com.atoss.idea.management.system.service;

public interface SendEmailService {
    /**
     * Sends an account activation email to the user associated with the given username.
     *
     * The method fetches the user details using the provided username and generates a random password
     * for the activation process. An activation email containing the generated password is sent to
     * the user's email address.
     *
     * After sending the email, the method updates the user's account information to set the hashed
     * password, activate the account, set the user role to standard, set an empty full name, and
     * associate the user with a default avatar.
     *
     * @param username The username of the user to whom the activation email will be sent.
     * @throws AvatarNotFoundException If the default avatar with ID 1 is not found in the avatar repository.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendApproveEmailToUser(String username);

    /**
     * Sends a registration rejection email to the user associated with the given username.
     *
     * The method fetches the user details using the provided username and sends an email notifying the user
     * that their registration request has been rejected.
     *
     * @param username The username of the user to whom the rejection email will be sent.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendDeclineEmailToUser(String username);

    /**
     * Sends an email to the admin regarding a login request received from the user associated with the given username.
     *
     * The method fetches the user details using the provided username and sends two emails:
     *   1. An email to all administrators alerting them about the login request.
     *   2. An email to the user's email address containing details of the login request.
     *
     * @param username The username of the user for whom the login request is received.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendEmailToAdmin(String username);

    /**
     * Sends an email to the user associated with the given username for the password reset request.
     *
     * The method fetches the user details using the provided username and sends an email containing
     * a password reset link and the newly generated password.
     *
     * @param username The username of the user for whom the password reset email will be sent.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    void sendEmailForgotPassword(String username);

    /**
     * Sends an account deactivation email to the user associated with the given username and deactivates the user's account.
     *
     * The method fetches the user details using the provided username and sends an account deactivation email
     * to the user's email address. After sending the email, it sets the user's account status to inactive (isActive = false)
     * and saves the updated user information to the repository.
     *
     * @param username The username of the user to whom the deactivation email will be sent.
     * @return True if the deactivation email is sent and the user's account is successfully deactivated; otherwise, false.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    boolean sendDeactivateEmailToUser(String username);

    /**
     * Sends an account reactivation email to the user associated with the given username and activates the user's account.
     *
     * The method fetches the user details using the provided username and generates a random password of length 15
     * characters using the `PasswordGenerator.generatePassayPassword()` method (not shown here). It then sends an account
     * reactivation email containing the reactivation link or the newly generated password to the user's email address.
     *
     * After sending the email, the method sets the user's account status to active (isActive = true) and changes the
     * user's password to the newly generated password (hashed using BCrypt) before saving the updated user information
     * to the repository.
     *
     * @param username The username of the user to whom the reactivation email will be sent.
     * @return True if the reactivation email is sent and the user's account is successfully reactivated; otherwise, false.
     * @throws RuntimeException If the user does not exist in the repository.
     * @throws RuntimeException If there are any issues during the email sending process.
     */
    boolean sendActivateEmailToUser(String username);
}
