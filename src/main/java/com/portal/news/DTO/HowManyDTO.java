package com.portal.news.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class HowManyDTO {
    private Long size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HowManyDTO that = (HowManyDTO) o;
        return Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }
}
