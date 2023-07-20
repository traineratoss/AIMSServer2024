package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.repository.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;


// This is the DTO we send from the server
@Data
public class IdeaResponseDTO {
    private User user;
    private String title;
    private String status;
    private List<CategoryDTO> categoryList;
    private Image image;
    private String text;
    private Date date;
}
