package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCommentReplyDTO {
    /**
     * This class will be used for returning replies of an existing comment
     *
     * @author FlorinCP
     */

    private String username;

    private Long parentId;

    private String commentText;

    // here we will display the diff. between creation date and fetch date
    // we will need some sort of method for that
    private String elapsedTime;

}
