package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "idea")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Idea {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idea_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private Status status;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private Date date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id")
    @JsonManagedReference(value = "idea-image")
    private Image image;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "idea_category",
            joinColumns = { @JoinColumn(name = "idea_id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id") }
    )
    @JsonIgnoreProperties("ideaList")
    private List<Category> categoryList;

    @JsonIgnoreProperties("ideas")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Lob
    @JsonIgnoreProperties("idea")
    @OneToMany(mappedBy = "idea", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @Override
    public String toString() {
        return "Idea{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", status='" + status + '\''
                + ", text='" + text + '\''
                + '}';
    }
}
