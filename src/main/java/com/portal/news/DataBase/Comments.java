package com.portal.news.DataBase;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
@Data
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
    @ManyToOne(fetch = FetchType.LAZY)
    private Posts post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;
    public Comments(String hrefToComment) {
        this.hrefToComment = hrefToComment;
    }
}
