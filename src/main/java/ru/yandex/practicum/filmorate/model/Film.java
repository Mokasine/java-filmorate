package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa = new Mpa(1, "G");
    private Set<Genre> genres = new TreeSet<>();
    private Set<Director> directors = new TreeSet<>();
    private Set<Integer> likes = new HashSet<>();
}
