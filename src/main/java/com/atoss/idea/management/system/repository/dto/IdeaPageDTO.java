package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Idea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaPageDTO {
    private int total;
    private Page<Idea> pagedIdeas;
}
