package com.atoss.idea.management.system.repository.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private String text;
    private List<IdeaRequestDTO> ideaList;
}
