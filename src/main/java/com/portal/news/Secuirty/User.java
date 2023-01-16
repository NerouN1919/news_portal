package com.portal.news.Secuirty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String login;
    private String password;
    private Set<Role> roles;

}
