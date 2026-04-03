package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final InMemoryStore store;

    @GetMapping
    public List<Genre> getAll() {
        return new ArrayList<>(store.getGenreMap().values());
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Integer id) {
        Genre genre = store.getGenreMap().get(id);
        if (genre == null) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
        return genre;
    }
}
