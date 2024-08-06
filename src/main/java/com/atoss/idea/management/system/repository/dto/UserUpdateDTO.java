package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String fullName;
    private String email;
    private Long avatarId;
    private ImageDTO image;
    private Boolean updatedImage;
}
