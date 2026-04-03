package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Director> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Integer id) { return service.getById(id); }

    @PostMapping
    public Director add(@RequestBody Director director) { return service.add(director); }

    @PutMapping
    public Director update(@RequestBody Director director) { return service.update(director); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
