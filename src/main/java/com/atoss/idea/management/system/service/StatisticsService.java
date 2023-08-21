package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.StatisticsDTO;

public interface StatisticsService {


    /**
     * Gets the general statistics of the application
     * Usage : in "/stats" controller , called when CustomStatistics is mounted
     *
     * @param mostCommentedSIdeasSortOrder sorting order of the ideas
     * @return a statistic DTO that contains all the field necessary for
     *         the statistics
     */
    StatisticsDTO getGeneralStatistics(String mostCommentedSIdeasSortOrder);


    /**
     * Returns the number of all replies between selected dates
     *
     * @param selectedDateFrom the date from
     * @param selectedDateTo the date to
     * @return the number of all replies between selected dates
     */
    Long getSelectionRepliesNumber(String selectedDateFrom, String selectedDateTo);

    /**
     * Returns the number of all comments between selected dates
     *
     * @param selectedDateFrom the date from
     * @param selectedDateTo the date to
     * @return the number of all comments between selected dates
     */
    Long getSelectionCommentNumber(String selectedDateFrom, String selectedDateTo);

    /**
     * used to retrieve stats based on given date interval
     * Function called by the "/filteredStats" controller when wanting
     * to display stats for selected time interval in CustomStatistics
     *
     * @param selectedDateFrom the date from within we want to get stats
     * @param selectedDateTo the date until  we want to get stats
     * @return used to retrieve stats based on given date interval
     */
    StatisticsDTO getStatisticsByDate(String selectedDateFrom,
                                      String selectedDateTo);


}
