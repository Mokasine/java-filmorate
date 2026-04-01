package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {
    private UserController controller;
    private User validUser;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test Name");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldAddValidUser() {
        User added = controller.addUser(validUser);
        assertNotNull(added.getId());
        assertEquals("test@example.com", added.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        validUser.setEmail("");
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));

        validUser.setEmail(null);
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotContainAt() {
        validUser.setEmail("testexample.com");
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenLoginIsBlank() {
        validUser.setLogin("");
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));

        validUser.setLogin(null);
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        validUser.setLogin("test login");
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsBlank() {
        validUser.setName("");
        User added = controller.addUser(validUser);
        assertEquals("testlogin", added.getName());

        validUser.setName(null);
        User added2 = controller.addUser(validUser);
        assertEquals("testlogin", added2.getName());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> controller.addUser(validUser));
    }

    @Test
    void shouldAllowBirthdayToday() {
        validUser.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> controller.addUser(validUser));
    }
}