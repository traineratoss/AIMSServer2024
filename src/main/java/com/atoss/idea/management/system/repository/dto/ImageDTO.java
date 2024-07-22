package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

import java.util.Base64;



@Data
public class ImageDTO {

    private Long id;
    private byte[] image;
    private String fileName;
    private String fileType;


    /**
     * Method is used to convert the byte array image data to a Base64 encoded string.
     *
     * @return convert byte array to a Base64 encoded string
     */
    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(image);
    }

}
