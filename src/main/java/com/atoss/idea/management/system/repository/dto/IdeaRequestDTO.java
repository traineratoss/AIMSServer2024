package com.atoss.idea.management.system.repository.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

// This is the DTO we receive from the client
@Data
public class IdeaRequestDTO {
    private String title;
    private String status;
    private List<CategoryDTO> categoryList; //BIG PROBLEM HERE!!!!!!!!!!!!!!!! DON'T EDIT
    private ImageDTO image;
    private String text;
    private Date date;
}
