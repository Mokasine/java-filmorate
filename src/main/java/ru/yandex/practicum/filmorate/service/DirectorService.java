package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.InMemoryDataStore;

import java.util.List;

@Service
public class DirectorService {
    private final InMemoryDataStore store;

    public DirectorService(InMemoryDataStore store) {
        this.store = store;
    }

    public List<Director> getAll() {
        return List.copyOf(store.directors.values());
    }

    public Director getById(Integer id) {
        Director director = store.directors.get(id);
        if (director == null) {
            throw new NotFoundException("Режиссер с id " + id + " не найден");
        }
        return director;
    }

    public Director add(Director director) {
        validate(director);
        director.setId(store.nextDirectorId());
        store.directors.put(director.getId(), director);
        return director;
    }

    public Director update(Director director) {
        if (director.getId() == null) {
            throw new ValidationException("ID режиссера должен быть указан");
        }
        getById(director.getId());
        validate(director);
        store.directors.put(director.getId(), director);
        return director;
    }

    public void delete(Integer id) {
        getById(id);
        store.directors.remove(id);
        store.films.values().forEach(f -> f.getDirectors().removeIf(d -> d.getId().equals(id)));
    }

    private void validate(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
    }
}
