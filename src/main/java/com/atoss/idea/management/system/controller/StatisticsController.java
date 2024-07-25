package com.atoss.idea.management.system.controller;


import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.dto.StatisticsDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.StatisticsService;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
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

        return new ResponseEntity<>(statisticsService.getGeneralStatistics(), HttpStatus.OK);

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

        return new ResponseEntity<>(statisticsService.getStatisticsByDate(
                selectedDateFrom,
                selectedDateTo), HttpStatus.OK);

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

        return ideaServiceImpl.findIdeasByIds(commentRepository.mostCommentedIdeasIdsByDate(selectedDateFrom, selectedDateTo));

    }

}
