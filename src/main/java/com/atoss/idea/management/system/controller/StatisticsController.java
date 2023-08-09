package com.atoss.idea.management.system.controller;


import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.dto.StatisticsDTO;
import com.atoss.idea.management.system.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aims/api/v1/statistics")
public class StatisticsController {


    private final StatisticsService statisticsService;


    private final CommentRepository commentRepository;


    /**
     * Constructor for the Statistics Controller
     *
     * @param statisticsService what it says
     * @param commentRepository ===
     */
    public StatisticsController(StatisticsService statisticsService, CommentRepository commentRepository) {
        this.statisticsService = statisticsService;
        this.commentRepository = commentRepository;
    }


    /**
     * Working on this
     *
     * @return stats for the ideas
     */
    @GetMapping("/stats")
    public ResponseEntity<StatisticsDTO> getStats() {

        return new ResponseEntity<>(statisticsService.getGeneralStatistics(), HttpStatus.OK);

    }

    /**
     * this method is used to return stats for selected time interval
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo data up to selection
     * @return statisticsDTO
     */
    @GetMapping("/filteredStats")
    public ResponseEntity<StatisticsDTO> getFilteredStats(
            @RequestParam(required = false) String selectedDateFrom,
            @RequestParam(required = false) String selectedDateTo) {

        return new  ResponseEntity<>(statisticsService.getStatisticsByDate(
                selectedDateFrom,
                selectedDateTo), HttpStatus.OK);

    }


    //    /**
    //     * ======
    //     *
    //     * @param selectedDateFrom ======
    //     * @param selectedDateTo ======
    //     * @return ======
    //     */
    //    @GetMapping("/test")
    //    public ArrayList<ArrayList<Long>> test(
    //        @RequestParam(required = false) String selectedDateFrom,
    //        @RequestParam(required = false) String selectedDateTo) {
    //
    //        List<Object[]> rObj = commentRepository.mostCommentedIdeasByDate(selectedDateFrom, selectedDateTo);
    //
    //        ArrayList<ArrayList<Long>>  result = new ArrayList<>();
    //
    //        for (Object[] obj : rObj) {
    //
    //            ArrayList<Long> intermediate = new ArrayList<>(2);
    //
    //            intermediate.add((Long) obj[0]);
    //            intermediate.add((Long) obj[1]);
    //
    //            result.add(intermediate);
    //        }
    //
    //
    //        return result;
    //    }

}
