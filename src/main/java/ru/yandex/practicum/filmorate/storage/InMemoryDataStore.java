package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryDataStore {
    public final Map<Integer, User> users = new LinkedHashMap<>();
    public final Map<Integer, Film> films = new LinkedHashMap<>();
    public final Map<Integer, Director> directors = new LinkedHashMap<>();
    public final Map<Integer, Review> reviews = new LinkedHashMap<>();
    public final Map<Integer, List<Event>> feed = new HashMap<>();

    public final Map<Integer, Genre> genres = new LinkedHashMap<>();
    public final Map<Integer, Mpa> mpas = new LinkedHashMap<>();

    private final AtomicInteger userId = new AtomicInteger(1);
    private final AtomicInteger filmId = new AtomicInteger(1);
    private final AtomicInteger directorId = new AtomicInteger(1);
    private final AtomicInteger reviewId = new AtomicInteger(1);
    private final AtomicLong eventId = new AtomicLong(1);

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

    public int nextUserId() { return userId.getAndIncrement(); }
    public int nextFilmId() { return filmId.getAndIncrement(); }
    public int nextDirectorId() { return directorId.getAndIncrement(); }
    public int nextReviewId() { return reviewId.getAndIncrement(); }
    public long nextEventId() { return eventId.getAndIncrement(); }
}
