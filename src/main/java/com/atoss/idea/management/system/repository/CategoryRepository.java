package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByText(String text);

    Category findCategoryById(Long id);
}
