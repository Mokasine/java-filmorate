package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final InMemoryStore store;

    @GetMapping
    public List<Director> getAll() {
        return new ArrayList<>(store.getDirectors().values());
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Integer id) {
        Director director = store.getDirectors().get(id);
        if (director == null) {
            throw new NotFoundException("Режиссёр с id=" + id + " не найден");
        }
        return director;
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        validate(director);
        director.setId(store.nextDirectorId());
        store.getDirectors().put(director.getId(), director);
        log.info("Создан режиссёр: {}", director);
        return director;
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        validate(director);
        if (director.getId() == null || !store.getDirectors().containsKey(director.getId())) {
            throw new NotFoundException("Режиссёр с id=" + director.getId() + " не найден");
        }
        store.getDirectors().put(director.getId(), director);
        log.info("Обновлён режиссёр: {}", director);
        return director;
    }

    private void validate(Director director) {
        if (director == null || director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссёра не может быть пустым");
        }
    }
}
