package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@AllArgsConstructor
@Data
public class RegDTO {
    private String name;
    private String surname;
    private String email;
    private String passwordHash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegDTO regDTO = (RegDTO) o;
        return Objects.equals(name, regDTO.name) && Objects.equals(surname, regDTO.surname) && Objects.equals(email, regDTO.email) && Objects.equals(passwordHash, regDTO.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, passwordHash);
    }
}
