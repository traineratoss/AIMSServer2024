package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageDTO {
    private int total;
    private Page<CommentDashboardResponseDTO> pagedComments;
}
