package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Role;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private boolean isActive;
    private Role role;
    private String username;
    private String fullName;
    private String email;
    private AvatarDTO avatar;
}
