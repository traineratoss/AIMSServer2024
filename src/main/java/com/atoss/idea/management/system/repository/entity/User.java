package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id")
    @JsonManagedReference(value = "user-avatar")
    private Avatar avatar;

    @Column(name = "is_active")
    private Boolean isActive;

    @JsonIgnoreProperties("user")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Idea> ideas;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference(value = "user-comments")
    private List<Comment> comments;

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", fullName='" + fullName + '\''
                + ", email='" + email + '\''
                + ", password='" + password + '\''
                + '}';
    }
}
