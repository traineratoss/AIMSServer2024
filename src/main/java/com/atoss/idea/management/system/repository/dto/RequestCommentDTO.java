package com.atoss.idea.management.system.repository.dto;

import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class RequestCommentDTO {

    private String username;

    private Long ideaId;

    private String commentText;

}
