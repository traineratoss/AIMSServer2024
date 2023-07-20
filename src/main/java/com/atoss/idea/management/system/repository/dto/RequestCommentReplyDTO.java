package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCommentReplyDTO {
    /**
     * This class will be used for adding replies to an existing comment
     *
     * @author FlorinCP
     */

    private String username;

    private Long parentId;

    private String commentText;

}
