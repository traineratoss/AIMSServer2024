package com.atoss.idea.management.system.repository.dto;

import lombok.Data;
import java.sql.Blob;

@Data
public class AvatarDTO {
    private Blob data;
}
