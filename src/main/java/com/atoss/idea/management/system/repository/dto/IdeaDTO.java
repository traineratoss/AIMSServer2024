package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.repository.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class IdeaDTO {
    private User user;
    private String title;
    private String status;
    private List<Category> categoryList;
    private Image image;
    private String text;
}
