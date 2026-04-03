package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStore;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new InMemoryStore());
    }

    @Test
    void shouldUseLoginAsNameWhenNameBlank() {
        User user = validUser();
        user.setName(" ");

        User created = userController.create(user);

        assertEquals("login", created.getName());
    }

    @Test
    void shouldFailWhenEmailWithoutAt() {
        User user = validUser();
        user.setEmail("mail.ru");

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        User user = validUser();
        user.setLogin("log in");

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    private User validUser() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 8, 20));
        return user;
    }
}
