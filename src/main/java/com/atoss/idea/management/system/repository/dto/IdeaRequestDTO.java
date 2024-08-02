package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// This is the DTO we receive from the client
@Data
public class IdeaRequestDTO {

    private String title;

    private Status status;

    private List<CategoryDTO> categoryList;

    private ImageDTO image;

    private String text;

    private Date creationDate;

    //private List<Rating> rating;

    private Double ratingAvg;

//    private List<MultipartFile> file;

}
