package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageDTO {
    private int total; //pt paginare
    private Page<CommentDashboardResponseDTO> pagedComments; //pt eu ma folosesc doar de contentul din comment si pt stergere e nevoie de id
}
