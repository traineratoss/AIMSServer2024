package com.atoss.idea.management.system.repository.dto;

import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class ResponseCommentDTO {

    private Long id;

    private String username;

    private Long ideaId;

    private String commentText;

    private boolean hasReplies;

    private String elapsedTime;

}
