package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class ImageDTO {

    private byte[] image;
    private String fileName;
    private String fileType;

}
