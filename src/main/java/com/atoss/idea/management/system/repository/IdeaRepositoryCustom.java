package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepositoryCustom {
    /**
     * This method filters all ideas by specific criteria
     * @param title  idea's title
     * @param text idea's text
     * @param status idea's status
     * @param categories idea's categories
     * @param user  idea's user
     * @param selectedDateFrom idea's the date from within you want to select
     * @param selectedDateTo idea's the date until you want to select
     * @param sortDirection idea's sort direction ASC DSC
     * @param username idk
     * @param pageable pagination
     * @return an object that contains paginated ideas that fulfill applied criteria.
     */
    IdeaPageDTO findIdeasByParameters(String title,
                                      String text,
                                      List<Status> status,
                                      List<String> categories,
                                      List<String> user,
                                      String selectedDateFrom,
                                      String selectedDateTo,
                                      String sortDirection,
                                      String username,
                                      Pageable pageable);

    List<Idea> findIdeasByDate(
            String selectedDateFrom,
            String selectedDateTo);




}
