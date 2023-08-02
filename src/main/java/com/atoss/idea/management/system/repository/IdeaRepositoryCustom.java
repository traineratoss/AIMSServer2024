package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import jakarta.persistence.criteria.*;

@Repository
public interface IdeaRepositoryCustom {
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

    List<Predicate> filterByDate(String selectedDateFrom, String selectedDateTo, Root<Idea> root, CriteriaBuilder cb);
}
