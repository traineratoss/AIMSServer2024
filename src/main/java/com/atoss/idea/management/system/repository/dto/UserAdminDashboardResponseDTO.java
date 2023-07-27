package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Role;
import lombok.Data;

@Data
public class UserAdminDashboardResponseDTO {
    private String username;
    private String email;
    private Role role;
    private Boolean isActive;
    private Boolean hasPassword;
}
