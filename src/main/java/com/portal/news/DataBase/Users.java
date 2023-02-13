package com.portal.news.DataBase;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(name="Gusers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "passwordHash")
    private String passwordHash;
    @Column(name = "email")
    private String email;
    @ManyToMany
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    List<Posts> likedPosts = new ArrayList<>();
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "user")
    private List<Comments> users = new ArrayList<>();
    public void addComment(Comments comments){
        comments.setUser(this);
        this.users.add(comments);
    }
    public void deleteComment(Comments comments){
        comments.setUser(null);
        this.users.remove(comments);
    }
    public void addLike(Posts posts){
        this.likedPosts.add(posts);
        posts.getLikes().add(this);
    }
    public void removeLike(Posts posts){
        this.likedPosts.remove(posts);
        posts.getLikes().remove(this);
    }
    public Users(String name, String surname, String passwordHash, String email) {
        this.name = name;
        this.surname = surname;
        this.passwordHash = passwordHash;
        this.email = email;
    }
}
