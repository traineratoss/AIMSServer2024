package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCommentDTO {

    private Long id;

    private String username;

    private Long ideaId;

    private String commentText;

    private boolean hasReplies;

    private List<ResponseCommentReplyDTO> replies;

    private String elapsedTime;

}
