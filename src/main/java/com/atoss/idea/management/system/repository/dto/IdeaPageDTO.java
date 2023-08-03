package com.atoss.idea.management.system.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

// THIS DTO IS USED FOR RETURNING THE PAGED IDEAS, BUT ALSO THE TOTAL NUMBER OF IDEAS USED IN THESE PAGINATIONS.
// THE NR OF TOTAL ELEMENTS ISN'T WORKING VERY WELL, SO THAT'S WHY I CREATED THIS DTO.
// WE COULD HAVE MADE A NEW ENDPOINT, BUT, IN THE FUTURE, WE COULD WANT TO RETURN MULTIPLE ITEMS, SO THIS DTO IS USEFUL.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaPageDTO {
    private int total;
    private Page<IdeaResponseDTO> pagedIdeas;
}
