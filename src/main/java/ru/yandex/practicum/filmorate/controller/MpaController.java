package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService service;

    public MpaController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mpa> getAll() { return service.getMpas(); }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable Integer id) { return service.getMpa(id); }
}
