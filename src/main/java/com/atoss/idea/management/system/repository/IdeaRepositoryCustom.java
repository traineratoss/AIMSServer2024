package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepositoryCustom {
    Page<Idea> findIdeasByParameters(String title, String text, List<Status> status, List<String> categories, List<String> user, Pageable pageable);
}
