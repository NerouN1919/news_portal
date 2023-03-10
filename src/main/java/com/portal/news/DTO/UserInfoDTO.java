package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private Boolean isAdmin;
}
