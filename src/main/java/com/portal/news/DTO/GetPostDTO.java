package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostDTO {
    private Long id;
    private Date date;
    private Long likes;
    private String title;
    private String pathToPhoto;
    private String text;
    private Boolean isLiked;
}
