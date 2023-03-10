package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddCommentDTO {
    private Long userId;
    private Long postId;
    private String comment;
}
