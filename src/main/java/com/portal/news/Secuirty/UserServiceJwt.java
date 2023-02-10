package com.portal.news.Secuirty;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceJwt {

    private final List<User> users;

    public UserServiceJwt() {
        this.users = Arrays.asList(
                new User("SviridenkoUser", "1234", Collections.singleton(Role.USER)),
                new User("MorozovUser", "1234", Collections.singleton(Role.USER)),
                new User("SviridenkoAdmin", "12345", Collections.singleton(Role.ADMIN)),
                new User("MorozovAdmin", "12345", Collections.singleton(Role.ADMIN))
        );
    }

    public Optional<User> getByLogin(@NonNull String login) {
        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst();
    }

}
