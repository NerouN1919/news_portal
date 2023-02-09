package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@AllArgsConstructor
@Data
public class ReturnedCommentDTO {
    private String comment;
    private Long postId;
    private Long user_id;
    private Date date;
}
