package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;


@Data
@Entity
@Table(name = "image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long id;

    @Lob
    @Column(name = "photo", length = 1000)
    private byte[] image;

    @OneToOne(mappedBy = "image")
    @JsonBackReference(value = "idea-image")
    private Idea idea;
    private String fileName;
    private String fileType;

    public Image(String fileName, String fileType, byte[] image) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.image = image;
    }
}
