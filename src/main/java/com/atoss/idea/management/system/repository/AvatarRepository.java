package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

}
