package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Review {
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private Integer useful = 0;
    private Set<Integer> likedBy = new HashSet<>();
    private Set<Integer> dislikedBy = new HashSet<>();
}
