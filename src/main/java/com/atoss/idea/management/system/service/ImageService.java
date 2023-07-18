package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageService extends JpaRepository<Image, Long> {
}
