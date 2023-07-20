package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCommentDTO {
    /**
     * This class will be used for creating comments
     *
     * @author FlorinCP
     */

    private String username;

    private Long ideaId;

    private String commentText;

}
