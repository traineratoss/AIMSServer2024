package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

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
}
