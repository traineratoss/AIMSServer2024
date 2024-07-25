package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Status;
import lombok.Data;

import java.util.Date;
import java.util.List;


// This is the DTO we send from the server
@Data
public class IdeaResponseDTO {

    private Long id;

    private String username;

    private String title;

    private Status status;

    private List<CategoryDTO> categoryList;

    private Integer commentsNumber;

    private ImageDTO image;

    private String text;

    private Date creationDate;

    private String elapsedTime;

    //private List<Rating> rating;
    private Double ratingAvg;
}

