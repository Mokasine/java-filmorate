package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {
    private FilmController controller;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
        validFilm = new Film();
        validFilm.setName("Test Film");
        validFilm.setDescription("Test Description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
    }

    @Test
    void shouldAddValidFilm() {
        Film added = controller.addFilm(validFilm);
        assertNotNull(added.getId());
        assertEquals("Test Film", added.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        validFilm.setName("");
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));

        validFilm.setName(null);
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        validFilm.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateTooEarly() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));
    }

    @Test
    void shouldAllowReleaseDateOnMinimumDate() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.addFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNotPositive() {
        validFilm.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));

        validFilm.setDuration(-10);
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));

        validFilm.setDuration(null);
        assertThrows(ValidationException.class, () -> controller.addFilm(validFilm));
    }
}