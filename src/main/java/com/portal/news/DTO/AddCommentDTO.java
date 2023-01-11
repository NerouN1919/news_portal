package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddCommentDTO {
    private Long UserId;
    private Long PostId;
    private String comment;
}
