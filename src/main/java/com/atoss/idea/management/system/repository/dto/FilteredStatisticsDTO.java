package com.atoss.idea.management.system.repository.dto;


import lombok.Data;

/**
 * This DTO is used to return statistics that have been filtered by custom input criteria
 * The idea is to send this DTO each time we change the desired filters
 */
@Data
public class FilteredStatisticsDTO {

    private Integer totalIdeas;
}
