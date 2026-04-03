package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Director implements Comparable<Director> {
    private Integer id;
    private String name;

    @Override
    public int compareTo(Director other) {
        return Integer.compare(this.id, other.id);
    }
}
