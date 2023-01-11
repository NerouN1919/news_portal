package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCommentDTO {
    private Long postId;
    private Long howMuch;
}
