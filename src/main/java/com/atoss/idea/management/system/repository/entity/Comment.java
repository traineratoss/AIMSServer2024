package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ToString
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @JsonBackReference(value = "user-comments")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", referencedColumnName = "idea_id", nullable = true)
    @JsonBackReference(value = "idea-comments")
    private Idea idea;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;

    @Column(name = "comment_text", length = 2000)
    private String commentText;

    @Column(name = "creation_date")
    private Date creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status")
    private ReviewStatus reviewStatus = ReviewStatus.NOT_REVIEWED;

    @ManyToMany(mappedBy = "likedComments")
    @JsonIgnoreProperties("likedComments")
    private List<User> userList = new ArrayList<>();


    @ManyToMany(mappedBy = "reportedComments")
    @JsonIgnoreProperties("reportedComments")
    private List<User> listOfUsers = new ArrayList<>();


}