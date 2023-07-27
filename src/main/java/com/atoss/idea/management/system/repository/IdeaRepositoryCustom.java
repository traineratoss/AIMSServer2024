package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepositoryCustom {
    IdeaPageDTO findIdeasByParameters(String title,
                                      String text,
                                      List<Status> status,
                                      List<String> categories,
                                      List<String> user,
                                      String sortDirection,
                                      Pageable pageable);
}
