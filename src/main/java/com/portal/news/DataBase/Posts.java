package com.portal.news.DataBase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "like")
    private Long like;
    @Column(name = "pathToPhoto")
    private String pathToPhoto;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();
    @Column(name = "title")
    private String title;
    @ManyToMany(mappedBy = "likedPosts")
    Set<Users> likes = new HashSet<>();
    public Posts(String pathToPhoto, String title) {
        like = (long)0;
        this.pathToPhoto = pathToPhoto;
        this.title = title;
    }
    public void addComment(Comments comments, Users users){
        comments.setPost(this);
        comments.setUser(users);
        users.addComment(comments);
        this.comments.add(comments);
    }
    private void deleteComment(Comments comments, Users users){
        comments.setPost(null);
        comments.setUser(null);
        users.deleteComment(comments);
        this.comments.remove(comments);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posts posts = (Posts) o;
        return Objects.equals(id, posts.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
