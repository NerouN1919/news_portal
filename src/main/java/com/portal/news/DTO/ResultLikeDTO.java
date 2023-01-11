package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultLikeDTO {
    private Long post_id;
    private Long likes;
}
