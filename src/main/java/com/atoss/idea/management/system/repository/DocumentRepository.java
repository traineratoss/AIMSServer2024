package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.dto.DocumentDTO;
import com.atoss.idea.management.system.repository.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByIdeaId(Long id);

    List<Document> findDocumentsByIdeaId(Long ideaId);
    void deleteById(Long id);
}
