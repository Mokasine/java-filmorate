package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final InMemoryStore store;

    @GetMapping
    public List<Mpa> getAll() {
        return new ArrayList<>(store.getMpaMap().values());
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable Integer id) {
        Mpa mpa = store.getMpaMap().get(id);
        if (mpa == null) {
            throw new NotFoundException("MPA с id=" + id + " не найден");
        }
        return mpa;
    }
}
