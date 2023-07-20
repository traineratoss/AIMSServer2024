package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avatar")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "avatar_id")
    private Long id;

    @Lob
    @Column(name = "data")
    private byte[] data;

    @OneToOne(mappedBy = "avatar")
    @JsonBackReference(value = "user-avatar")
    private User user;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_type")
    private String fileType;

    public Avatar(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
