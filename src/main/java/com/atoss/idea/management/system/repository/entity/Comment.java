package com.atoss.idea.management.system.repository.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import java.util.Date;

@ToString
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "idea_id")
    private Long ideaId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "creation_date")
    private Date creationDate;

}
