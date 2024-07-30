package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

import java.util.Base64;



@Data
public class DocumentDTO {

    private Long id;
    private byte[] document;
    private String fileName;
    private String fileType;
    private Long ideaId;
    private Long userId;


    /**
     * Method is used to convert the byte array document data to a Base64 encoded string.
     *
     * @return convert byte array to a Base64 encoded string
     */
    public String getBase64Document() {
        return Base64.getEncoder().encodeToString(document);
    }

}
