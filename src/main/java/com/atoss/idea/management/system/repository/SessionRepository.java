package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}
