package com.portal.news.DataBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "hrefToComment")
    private String hrefToComment;
    @Column(name = "date")
    private Date date = new Date();
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Posts post;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;
    public Comments(String hrefToComment) {
        this.hrefToComment = hrefToComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comments comments = (Comments) o;
        return Objects.equals(id, comments.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
