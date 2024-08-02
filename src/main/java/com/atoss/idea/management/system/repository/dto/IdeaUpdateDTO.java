package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Status;
import lombok.Data;

import java.util.List;

@Data
public class IdeaUpdateDTO {
    private String title;
    private Status status;
    private List<CategoryDTO> categoryList;
    private ImageDTO image;
    private String text;
}
