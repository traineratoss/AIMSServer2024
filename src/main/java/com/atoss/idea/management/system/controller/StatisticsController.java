package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.dto.StatisticsDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.StatisticsService;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/aims/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    private final CommentRepository commentRepository;

    private final IdeaRepository ideaRepository;

    private final IdeaServiceImpl ideaServiceImpl;

    /**
     * Constructor for the Statistics Controller
     *
     * @param statisticsService used for injecting the StatisticsService in order to access and use its methods
     * @param commentRepository repository for the Comment Entity
     * @param ideaRepository repository for the Idea Entity
     * @param ideaServiceImpl Idea Service Implementation
     */
    public StatisticsController(StatisticsService statisticsService,
                                CommentRepository commentRepository,
                                IdeaRepository ideaRepository,
                                IdeaServiceImpl ideaServiceImpl) {
        this.statisticsService = statisticsService;
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
        this.ideaServiceImpl = ideaServiceImpl;
    }

    /**
     * Return statistics generated for the entire app since the beginning.
     * Used when powering up the statistics view , default data to be shown
     *
     * @return general stats for the ideas
     */
    @GetMapping("/stats")
    public ResponseEntity<StatisticsDTO> getStats() {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve general statistics");
        }

        StatisticsDTO stats = statisticsService.getGeneralStatistics();

        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved general statistics");
        }

        return new ResponseEntity<>(stats, HttpStatus.OK);

    }

    /**
     * this method is used to return stats for selected time interval
     * Usage: when wanting to display stats for selected time interval in CustomStatistics
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo data up to selection
     * @return statisticsDTO
     */
    @GetMapping("/filteredStats")
    public ResponseEntity<StatisticsDTO> getFilteredStats(
            @RequestParam(required = false) String selectedDateFrom,
            @RequestParam(required = false) String selectedDateTo) {

        if (log.isInfoEnabled()) {
            log.info("Received request to filter statistics from {} to {}", selectedDateFrom, selectedDateTo);
        }

        StatisticsDTO stats = statisticsService.getStatisticsByDate(selectedDateFrom, selectedDateTo);

        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved filtered stats");
        }

        return new ResponseEntity<>(stats, HttpStatus.OK);

    }

    /**
     * Testing endpoint
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo data up to selection
     * @return testing endpoint
     */
    @GetMapping("/test")
    public List<Idea> getIdeaList(@RequestParam(required = false) String selectedDateFrom,
                                  @RequestParam(required = false) String selectedDateTo) {

        if (log.isInfoEnabled()) {
            log.info("Received request for idea list from {} to {}", selectedDateFrom, selectedDateTo);
        }

        List<Idea> ideas = ideaServiceImpl.findIdeasByIds(commentRepository.mostCommentedIdeasIdsByDate(selectedDateFrom, selectedDateTo));

        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved idea list");
        }

        return ideas;

    }

}
