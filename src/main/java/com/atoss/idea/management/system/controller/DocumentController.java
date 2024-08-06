package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.DocumentNotFoundException;
import com.atoss.idea.management.system.repository.dto.DocumentDTO;
import com.atoss.idea.management.system.service.DocumentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
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
     * @param files the multipart file representing the document to be uploaded.
     * @param ideaId id of the idea to which the documents are attached
     * @param userId id of the user which attached the documents
     * @return it returns an DocumentDTO that represents the added document.
     * @throws IOException it throws when the I/O operation fails, or it was interrupted.
     */
    @PostMapping("/addDocument")
    public ResponseEntity<List<DocumentDTO>> addDocument(@RequestParam("files") MultipartFile[] files,
                                                         @RequestParam Long ideaId,
                                                         @RequestParam Long userId) throws IOException {
        return new ResponseEntity<>(documentService.addDocument(files, ideaId, userId), HttpStatus.OK);
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
    public ResponseEntity<byte[]> getDocument(@RequestParam Long id) throws DocumentNotFoundException {
        DocumentDTO document = documentService.getDocument(id);
        byte[] fileContent = document.getDocument();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(document.getFileName())
                .build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
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
     * @param id the id of the idea where the document is attached
     * @return it returns a response entity with confirmation.
     */
    @Transactional
    @DeleteMapping("/deleteDocument")
    public ResponseEntity<String> deleteDocumentByIds(@RequestParam Long id) {
        documentService.deleteDocumentById(id);
        return new ResponseEntity<>("Document deleted successfully", HttpStatus.OK);
    }
}