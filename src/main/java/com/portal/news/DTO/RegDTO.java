package com.portal.news.DTO;

import lombok.Data;

@Data
public class RegDTO {
    private String name;
    private String surname;
    private String email;
    private String passwordHash;
}
