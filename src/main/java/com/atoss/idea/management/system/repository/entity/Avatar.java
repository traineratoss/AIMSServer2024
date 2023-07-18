package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.sql.Blob;

@Data
@Entity
@Table(name = "avatar")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "avatar_id")
    private Long avatarId;

    @Lob
    @Column(name = "data")
    private Blob data;

    @OneToOne(mappedBy = "avatar")
    @JsonManagedReference
    private User user;
}
