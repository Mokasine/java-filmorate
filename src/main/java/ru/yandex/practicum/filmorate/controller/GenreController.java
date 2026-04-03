package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService service;

    public GenreController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Genre> getAll() { return service.getGenres(); }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Integer id) { return service.getGenre(id); }
}
