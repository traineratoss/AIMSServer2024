package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.FilteredStatisticsDTO;
import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.dto.StatisticsDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IdeaService {
    IdeaResponseDTO addIdea(IdeaRequestDTO idea, String username);

    IdeaResponseDTO getIdeaById(Long id);

    IdeaResponseDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO);

    void deleteIdeaById(Long id);

    IdeaPageDTO getAllIdeas(Pageable pageable);

    IdeaPageDTO getAllIdeasByUserUsername(String username, Pageable pageable);

    /**
     * This method filters all ideas by specific criteria
     * @param title  idea's title
     * @param text idea's text
     * @param status idea's statuses , originally String but split as AList of String
     * @param categories idea's categories
     * @param user  This is a string that has commas innit, and it will be split in code at some point , to be renamed accordingly
     *              this is basically the selections of users that you want to see ideas of as you are logged as an Admin
     * @param selectedDateFrom idea's the date from within you want to select
     * @param selectedDateTo idea's the date until you want to select
     * @param sortDirection idea's sort direction ASC DSC
     * @param username in case of MyIdeas view , the username will be taken from localStorage
     * @param pageable pagination
     * @return an object that contains paginated ideas that fulfill applied criteria.
     */
    IdeaPageDTO filterIdeasByAll(String title,
                                 String text,
                                 List<Status> status,
                                 List<String> categories,
                                 List<String> user,
                                 String selectedDateFrom,
                                 String selectedDateTo,
                                 String sortDirection,
                                 String username,
                                 Pageable pageable);

    FilteredStatisticsDTO getStatisticsByDate(List<Status> statuses,
                                              List<String> categories,
                                              List<String> users,
                                              String selectedDateFrom,
                                              String selectedDateTo);

    StatisticsDTO getGeneralStatistics();

    FilteredStatisticsDTO getFilteredStatistics(IdeaPageDTO ideaPageDTO);

    List<Predicate> filterByDate(String selectedDateFrom, String selectedDateTo, Root<Idea> root, CriteriaBuilder cb);

}