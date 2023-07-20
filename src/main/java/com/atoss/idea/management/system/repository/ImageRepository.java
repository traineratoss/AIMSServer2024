package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findImageById(Long id);
}