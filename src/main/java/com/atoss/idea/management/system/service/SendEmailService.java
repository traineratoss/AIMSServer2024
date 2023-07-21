package com.atoss.idea.management.system.service;

public interface SendEmailService {
    void sendEmailToUser(String username);

    void sendEmailToAdmin(String username);
}
