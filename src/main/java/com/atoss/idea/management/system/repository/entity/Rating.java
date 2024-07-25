package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "rating")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rating_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", referencedColumnName = "idea_id")
    @JsonBackReference
    private Idea idea;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "rating")
    private Double rating;

    @Override
    public String toString() {
        return "Rating{"
                + "id=" + id
                + ", idea=" + idea
                + ", user=" + user
                + ", rating=" + rating
                + '}';
    }
}
