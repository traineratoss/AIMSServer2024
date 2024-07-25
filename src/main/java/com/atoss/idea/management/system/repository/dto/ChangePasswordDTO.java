package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String username;
    private String oldPassword;
    private String newPassword;

}
