package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldCreateFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Sci-fi");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        Film created = filmController.create(film);

        assertNotNull(created.getId());
    }

    @Test
    void shouldFailWhenNameBlank() {
        Film film = validFilm();
        film.setName(" ");

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldFailWhenReleaseDateTooEarly() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldAcceptBoundaryReleaseDate() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = validFilm();
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    private Film validFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }
}
