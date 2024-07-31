package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.DocumentNotFoundException;
import com.atoss.idea.management.system.repository.dto.DocumentDTO;
import com.atoss.idea.management.system.repository.entity.Document;
import com.atoss.idea.management.system.service.DocumentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aims/api/v1/documents")

public class DocumentController {
    private final DocumentService documentService;

    /**
     * Constructor
     *
     * @param documentService Dependency injection through constructor
     */
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Uploads an document to the database in the form of an DocumentDTO.
     *
     * @param file the multipart file representing the document to be uploaded.
     * @return it returns an DocumentDTO that represents the added document.
     * @throws IOException it throws when the I/O operation fails, or it was interrupted.
     */
    @PostMapping("/addDocument")
    public ResponseEntity<DocumentDTO> addDocument(@RequestBody MultipartFile file, @RequestParam Long ideaId, @RequestParam Long userId) throws IOException {
        return new ResponseEntity<>(documentService.addDocument(file, ideaId, userId), HttpStatus.OK);
    }


    /**
     * Gets a document from the database by the provided document id
     *
     * @param id it is for getting the document by id.
     * @return it returns a response entity with the selected document by the id of the document.
     * @throws DocumentNotFoundException if the document we want to get doesn't exist into the database
     *                                it will throw an exception
     */
    @GetMapping("/get")
    public ResponseEntity<DocumentDTO> getDocument(@RequestParam Long id) throws DocumentNotFoundException {
        return new ResponseEntity<>(documentService.getDocument(id), HttpStatus.OK);
    }

    /**
     *  Gets an document by an idea id
     *
     * @param ideaId the id of the idea
     * @return it returns a response entity with the document.
     */
    @GetMapping("/getByIdea")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByIdeaId(@RequestParam Long ideaId) {
        return new ResponseEntity<>(documentService.getDocumentsByIdeaId(ideaId), HttpStatus.OK);
    }

    /**
     *  Deletes a document by the id of the user who attached it and the id of the idea where it is attached
     *
     * @param ideaId the id of the idea where the document is attached
     * @param userId the id of the user who attached the document
     * @return it returns a response entity with confirmation.
     */
    @Transactional
    @DeleteMapping("/deleteDocument")
    public ResponseEntity<String> deleteDocumentByIds(@RequestParam Long ideaId, @RequestParam Long userId) {
        documentService.removeDocument(ideaId, userId);
        return new ResponseEntity<>("Document deleted successfully", HttpStatus.OK);
    }
}
