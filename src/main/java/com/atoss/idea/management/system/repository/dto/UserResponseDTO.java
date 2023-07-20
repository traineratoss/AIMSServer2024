package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private AvatarDTO avatar;
    private List<IdeaRequestDTO> ideas;
}
