package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryStore {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private final Map<Integer, Director> directors = new LinkedHashMap<>();
    private final Map<Integer, Mpa> mpaMap = new LinkedHashMap<>();
    private final Map<Integer, Genre> genreMap = new LinkedHashMap<>();

    private final AtomicInteger userId = new AtomicInteger(1);
    private final AtomicInteger filmId = new AtomicInteger(1);
    private final AtomicInteger directorId = new AtomicInteger(1);

    public InMemoryStore() {
        mpaMap.put(1, new Mpa(1, "G"));
        mpaMap.put(2, new Mpa(2, "PG"));
        mpaMap.put(3, new Mpa(3, "PG-13"));
        mpaMap.put(4, new Mpa(4, "R"));
        mpaMap.put(5, new Mpa(5, "NC-17"));

        genreMap.put(1, new Genre(1, "Комедия"));
        genreMap.put(2, new Genre(2, "Драма"));
        genreMap.put(3, new Genre(3, "Мультфильм"));
        genreMap.put(4, new Genre(4, "Триллер"));
        genreMap.put(5, new Genre(5, "Документальный"));
        genreMap.put(6, new Genre(6, "Боевик"));
    }

    public Map<Integer, User> getUsers() { return users; }
    public Map<Integer, Film> getFilms() { return films; }
    public Map<Integer, Director> getDirectors() { return directors; }
    public Map<Integer, Mpa> getMpaMap() { return mpaMap; }
    public Map<Integer, Genre> getGenreMap() { return genreMap; }

    public int nextUserId() { return userId.getAndIncrement(); }
    public int nextFilmId() { return filmId.getAndIncrement(); }
    public int nextDirectorId() { return directorId.getAndIncrement(); }

    public Set<Genre> normalizeGenres(Set<Genre> source) {
        Set<Genre> result = new TreeSet<>();
        if (source == null) {
            return result;
        }
        for (Genre genre : source) {
            if (genre != null && genre.getId() != null) {
                Genre known = genreMap.get(genre.getId());
                if (known != null) {
                    result.add(known);
                }
            }
        }
        return result;
    }

    public Set<Director> normalizeDirectors(Set<Director> source) {
        Set<Director> result = new TreeSet<>();
        if (source == null) {
            return result;
        }
        for (Director director : source) {
            if (director != null && director.getId() != null) {
                Director known = directors.get(director.getId());
                if (known != null) {
                    result.add(known);
                }
            }
        }
        return result;
    }
}
