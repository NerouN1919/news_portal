package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddPostDTO {
    private String title;
    private String imagePath;
}
