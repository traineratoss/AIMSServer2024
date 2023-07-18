package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.User;
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
    private User user;

    // to be replaced with ideaDTO
    private Idea idea;

    private Long parentId;

    private String commentText;

    private Date creationDate;

}
