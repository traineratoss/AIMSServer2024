package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Finds a Category object by its associated text.
     *
     * @param text it's the category text that needs to be found.
     * @return the Category object that matches the provided text.
     */
    Category findByText(String text);

}
