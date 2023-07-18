package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Date;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long id;

    // to be replaced with userDTO
    private UserDTO user;

    // to be replaced with ideaDTO
    private IdeaDTO idea;

    private Long parentId;

    private String commentText;

    private Date creationDate;

}
