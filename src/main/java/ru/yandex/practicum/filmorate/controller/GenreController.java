package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> getAll() {
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Integer id) {
        return filmService.getGenreById(id);
    }
}
