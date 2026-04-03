package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Mpa> getAll() {
        return filmService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable Integer id) {
        return filmService.getMpaById(id);
    }
}
