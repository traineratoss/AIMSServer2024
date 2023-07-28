package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Idea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

// THIS DTO IS USED FOR RETURNING THE PAGED IDEAS, BUT ALSO THE TOTAL NUMBER OF IDEAS USED IN THESE PAGINATIONS.
// WE COULD HAVE MADE A NEW ENDPOINT, BUT, IN THE FUTURE, WE COULD WANT TO RETURN MULTIPLE ITEMS, SO THIS DTO IS USEFUL.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaPageDTO {
    private int total;
    private Page<Idea> pagedIdeas;
}
