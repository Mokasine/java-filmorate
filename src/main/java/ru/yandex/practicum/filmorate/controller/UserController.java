package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAllUsers() { return service.getAll(); }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) { return service.getById(id); }

    @PostMapping
    public User addUser(@RequestBody User user) { return service.add(user); }

    @PutMapping
    public User updateUser(@RequestBody User user) { return service.update(user); }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) { service.delete(id); }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) { service.addFriend(id, friendId); }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) { service.removeFriend(id, friendId); }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) { return service.getFriends(id); }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return service.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer id) { return service.getRecommendations(id); }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Integer id) { return service.getFeed(id); }

    @GetMapping("/{id}/films/common")
    public List<Film> getCommonFilms(@PathVariable Integer id, @RequestParam Integer friendId) {
        return service.getCommonFilms(id, friendId);
    }
}
