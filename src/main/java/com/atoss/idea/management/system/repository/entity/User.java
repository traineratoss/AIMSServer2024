package com.atoss.idea.management.system.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.List;


@Data
@Entity
@Table(name = "\"user\"")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Unique
    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Unique
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "has_password")
    private Boolean hasPassword;

    @Column(name = "role")
    private Role role;

    @Column(name = "otp")
    @Embedded
    private OTP otp;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", referencedColumnName = "avatar_id")
    @JsonBackReference(value = "users-avatar")
    private Avatar avatar;

    @Column(name = "is_active")
    private Boolean isActive;

    @JsonIgnoreProperties("user")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Idea> ideas;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference(value = "user-comments")
    private List<Comment> comments;

    @Column(name = "is_first_login")
    private Boolean isFirstLogin;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
            name = "likes",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "comment_id") }
    )
    @JsonIgnoreProperties("userList")
    private List<Comment> likedComments;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Rating> rating;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Subscription> isSubscribed;


    /**
     * Constructor for creating a User object with the provided username and email.
     *
     * @param username The username of the user to be created.
     * @param email    The email address of the user to be created.
     *
     * @see User
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void setUsername(String username)
    {
        this.username=username;
    }
    public String getUsername()
    {
        return this.username;
    }



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
