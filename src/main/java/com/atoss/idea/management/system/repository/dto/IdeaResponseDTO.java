package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;


// This is the DTO we send from the server
@Data
public class IdeaResponseDTO {
    private String title;
    private String status;
    private List<CategoryDTO> categoryList;
    private ImageDTO image;
    private String text;
    private Date date;
}
