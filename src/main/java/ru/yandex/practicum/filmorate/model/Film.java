package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>();
    private Set<Director> directors = new TreeSet<>();

    @JsonIgnore
    private Set<Integer> likes = new TreeSet<>();
}
