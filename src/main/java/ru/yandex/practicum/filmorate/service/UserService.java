package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryDataStore;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final InMemoryDataStore store;

    public UserService(InMemoryDataStore store) {
        this.store = store;
    }

    public List<User> getAll() {
        return List.copyOf(store.users.values());
    }

    public User getById(Integer id) {
        User user = store.users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public User add(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(store.nextUserId());
        if (user.getFriends() == null) {
            user.setFriends(new java.util.HashSet<>());
        }
        store.users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("ID пользователя должен быть указан");
        }
        getById(user.getId());
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(store.users.get(user.getId()).getFriends());
        }
        store.users.put(user.getId(), user);
        return user;
    }

    public void delete(Integer id) {
        getById(id);
        store.users.remove(id);
        store.users.values().forEach(u -> u.getFriends().remove(id));
        store.films.values().forEach(f -> f.getLikes().remove(id));
        store.reviews.values().removeIf(r -> Objects.equals(r.getUserId(), id));
        store.feed.remove(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        User user = getById(id);
        User friend = getById(friendId);
        user.getFriends().add(friendId);
        addEvent(id, friendId, "FRIEND", "ADD");
    }

    public void removeFriend(Integer id, Integer friendId) {
        User user = getById(id);
        getById(friendId);
        user.getFriends().remove(friendId);
        addEvent(id, friendId, "FRIEND", "REMOVE");
    }

    public List<User> getFriends(Integer id) {
        return getById(id).getFriends().stream()
                .map(this::getById)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        User user = getById(id);
        User other = getById(otherId);
        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::getById)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public List<Event> getFeed(Integer userId) {
        getById(userId);
        return store.feed.getOrDefault(userId, List.of());
    }

    public List<Film> getRecommendations(Integer userId) {
        getById(userId);
        var targetLikes = store.films.values().stream()
                .filter(f -> f.getLikes().contains(userId))
                .map(Film::getId)
                .collect(Collectors.toSet());

        int bestUserId = -1;
        int bestScore = 0;
        for (User other : store.users.values()) {
            if (Objects.equals(other.getId(), userId)) continue;
            int score = (int) store.films.values().stream()
                    .filter(f -> f.getLikes().contains(other.getId()) && targetLikes.contains(f.getId()))
                    .count();
            if (score > bestScore) {
                bestScore = score;
                bestUserId = other.getId();
            }
        }
        if (bestUserId == -1) {
            return List.of();
        }
        final int recommendedFrom = bestUserId;
        return store.films.values().stream()
                .filter(f -> f.getLikes().contains(recommendedFrom) && !f.getLikes().contains(userId))
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        getById(userId);
        getById(friendId);
        return store.films.values().stream()
                .filter(f -> f.getLikes().contains(userId) && f.getLikes().contains(friendId))
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed()
                        .thenComparing(Film::getId))
                .collect(Collectors.toList());
    }

    public void addEvent(Integer userId, Integer entityId, String eventType, String operation) {
        store.feed.computeIfAbsent(userId, k -> new java.util.ArrayList<>())
                .add(new Event(store.nextEventId(), userId, entityId, eventType, operation, System.currentTimeMillis()));
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ @ и не быть пустым");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
