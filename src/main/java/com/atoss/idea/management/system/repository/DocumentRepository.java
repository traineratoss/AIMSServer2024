package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    /**
     * Retrieves the documents attached to the given idea
     *
     * @param ideaId id of the idea to  which the documents are attached
     * @return the list of documents
     */
    List<Document> findDocumentsByIdeaId(Long ideaId);

    /**
     * Retrieves the documents attached to the given idea
     *
     * @param id id of the idea to  which the documents are attached
     */
    void deleteById(Long id);

    /**
     * Retrieves documents attached to the given idea by the given user
     *
     * @param ideaId id of the idea to which the documents are attached
     * @param userId id of the user who attached the documents
     * @return the list of documents
     */
    Optional<Document> findByIdeaIdAndUserId(Long ideaId, Long userId);

    /**
     * Deletes the document added by a given user to a given idea
     *
     * @param ideaId id of the idea to which the documents are attached
     * @param userId id of the user who attached the documents
     */
    void deleteByIdeaIdAndUserId(Long ideaId, Long userId);
}

