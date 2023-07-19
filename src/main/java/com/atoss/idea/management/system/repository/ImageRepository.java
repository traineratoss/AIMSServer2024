package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}