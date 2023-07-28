package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Role;
import lombok.Data;

@Data
public class UserSecurityDTO {
    private String username;
    private Role role;
}
