package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostDTO {
    private Long id;
    private Long likes;
    private String title;
    private String pathToPhoto;
}
