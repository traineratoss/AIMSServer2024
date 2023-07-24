package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Lob
    @Column(name = "photo", length = 1000)
    private byte[] image;

    @OneToMany(mappedBy = "image")
    @JsonBackReference(value = "idea-image")
    private List<Idea> ideas;
    private String fileName;
    private String fileType;

    public Image(String fileName, String fileType, byte[] image) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.image = image;
    }
}
