package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class CommentStatisticsDTO {
    private Long commentId;
    private String commentText;
    private Integer nrLikes;
}
