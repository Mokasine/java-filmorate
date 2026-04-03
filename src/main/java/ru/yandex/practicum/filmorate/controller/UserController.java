package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final InMemoryStore store;

    @PostMapping
    public User create(@RequestBody User user) {
        validateUser(user);
        normalizeName(user);
        user.setId(store.nextUserId());
        store.getUsers().put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validateUser(user);
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }
        User existing = getUserOrThrow(user.getId());
        normalizeName(user);
        user.setFriends(existing.getFriends());
        store.getUsers().put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(store.getUsers().values());
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return getUserOrThrow(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        User user = getUserOrThrow(id);
        getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        log.info("Пользователь {} добавил в друзья {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        User user = getUserOrThrow(id);
        getUserOrThrow(friendId);
        user.getFriends().remove(friendId);
        log.info("Пользователь {} удалил из друзей {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        User user = getUserOrThrow(id);
        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        User user = getUserOrThrow(id);
        User other = getUserOrThrow(otherId);
        Set<Integer> otherFriends = other.getFriends();
        return user.getFriends().stream()
                .filter(otherFriends::contains)
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Integer id) {
        User user = store.getUsers().get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
