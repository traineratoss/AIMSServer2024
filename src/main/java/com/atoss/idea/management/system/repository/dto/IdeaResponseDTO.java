package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Status;
import lombok.Data;

import java.util.Date;
import java.util.List;


// This is the DTO we send from the server
@Data
public class IdeaResponseDTO {
    private Long id;
    private String title;
    private Status status;
    private List<CategoryDTO> categoryList;
    private ImageDTO image;
    private String text;
    private Date date;
}
