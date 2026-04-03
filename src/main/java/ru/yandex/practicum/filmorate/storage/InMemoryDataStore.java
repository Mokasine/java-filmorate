package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class InMemoryDataStore {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private final Map<Integer, Director> directors = new LinkedHashMap<>();
    private final Map<Integer, Review> reviews = new LinkedHashMap<>();
    private final Map<Integer, Event> events = new LinkedHashMap<>();
    private final Map<Integer, Genre> genres = new LinkedHashMap<>();
    private final Map<Integer, Mpa> mpas = new LinkedHashMap<>();

    private int nextUserId = 1;
    private int nextFilmId = 1;
    private int nextDirectorId = 1;
    private int nextReviewId = 1;
    private int nextEventId = 1;

    public InMemoryDataStore() {
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));

        mpas.put(1, new Mpa(1, "G"));
        mpas.put(2, new Mpa(2, "PG"));
        mpas.put(3, new Mpa(3, "PG-13"));
        mpas.put(4, new Mpa(4, "R"));
        mpas.put(5, new Mpa(5, "NC-17"));
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }

    public Map<Integer, Director> getDirectors() {
        return directors;
    }

    public Map<Integer, Review> getReviews() {
        return reviews;
    }

    public Map<Integer, Event> getEvents() {
        return events;
    }

    public Map<Integer, Genre> getGenres() {
        return genres;
    }

    public Map<Integer, Mpa> getMpas() {
        return mpas;
    }

    public int nextUserId() {
        return nextUserId++;
    }

    public int nextFilmId() {
        return nextFilmId++;
    }

    public int nextDirectorId() {
        return nextDirectorId++;
    }

    public int nextReviewId() {
        return nextReviewId++;
    }

    public int nextEventId() {
        return nextEventId++;
    }
}
