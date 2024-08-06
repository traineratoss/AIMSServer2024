package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.DocumentNotFoundException;
import com.atoss.idea.management.system.repository.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    /**
     * This method will save the document that is received as a parameter in the database.
     *
     * @param file it is for the document we receive from the client.
     * @param ideaId the id of the idea to which the document is attached
     * @param userId the id of the user who attached the document
     * @return it returns an DocumentDTO that represents the added document data.
     * @throws IOException it throws when the I/O operation fails, or it was interrupted.
     */
    List<DocumentDTO> addDocument(MultipartFile[] file, Long ideaId, Long userId) throws IOException;

    /**
     * Gets the document that is requested by the id of the document.
     *
     * @param id it is for getting the document by id.
     * @return it returns an DocumentDto.
     * @throws DocumentNotFoundException if the document we want to get doesn't exist into the database
     *                                it will throw an exception.
     */
    DocumentDTO getDocument(Long id) throws DocumentNotFoundException;


    /**
     *  Gets a document by an idea id
     *
     * @param ideaId the id of the idea
     * @return it returns the document.
     */
    List<DocumentDTO> getDocumentsByIdeaId(Long ideaId);

    /**
     *  Removes a document from the DB by the id of the user who uploaded it and the id of the idea where it is uploaded
     *
     * @param id the id of the doc
     */

    void deleteDocumentById(Long id);
}

