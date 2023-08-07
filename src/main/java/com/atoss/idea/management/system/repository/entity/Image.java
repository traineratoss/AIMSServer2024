package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Entity
@Table(name = "image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "photo", length = 1000)
    private byte[] image;

    @OneToMany(mappedBy = "image")
    @JsonBackReference(value = "idea-image")
    private List<Idea> idea;
    private String fileName;
    private String fileType;

    /**
     * Constructor
     *
     * @param fileName the name of the file.
     * @param fileType the file format of the file.
     * @param image the byte array of the image data.
     */
    public Image(String fileName, String fileType, byte[] image) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.image = image;
    }
}
