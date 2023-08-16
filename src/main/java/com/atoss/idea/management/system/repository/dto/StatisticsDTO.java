package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

import java.util.List;

/**
 * This DTO is used to return statistics about all ideas.
 */
@Data
public class StatisticsDTO {

    private Long nrOfUsers;

    private Long nrOfIdeas;

    private Long totalNrOfComments;

    private Long totalNrOfReplies;

    private Double ideasPerUser;

    private Long implementedIdeas;

    private Long draftIdeas;

    private Long openIdeas;

    private Double draftP;

    private Double openP;

    private Double implP;

    private List<IdeaResponseDTO> mostCommentedIdeas;

}
