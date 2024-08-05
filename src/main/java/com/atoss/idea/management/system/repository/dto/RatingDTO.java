package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class RatingDTO {
    private Long userId;
    private Long ideaId;
    private Double rating;
}
