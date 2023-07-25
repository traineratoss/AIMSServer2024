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
    /**
     * This class will be used for returning comments
     *
     * @author FlorinCP
     */
    private Long id;

    private String username;

    private Long ideaId;

    private String commentText;

    private boolean hasReplies;
    // to be  deleted later
    private List<ResponseCommentReplyDTO> replies;

    private String elapsedTime;

}
