package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@AllArgsConstructor
@Data
public class IdDTO {
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdDTO idDTO = (IdDTO) o;
        return Objects.equals(id, idDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
