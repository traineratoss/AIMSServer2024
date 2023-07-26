package com.atoss.idea.management.system.service;

public interface SendEmailService {
    void sendApproveEmailToUser(String username);

    void sendDeclineEmailToUser(String username);

    void sendEmailToAdmin(String username);

    void sendEmailForgotPassword(String username);

    boolean sendDeactivateEmailToUser(String username);

    boolean sendActivateEmailToUser(String username);
}
