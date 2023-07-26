package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

import java.util.Base64;

@Data
public class ImageDTO {

    private byte[] image;
    private String fileName;
    private String fileType;

    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(image);
    }
}
