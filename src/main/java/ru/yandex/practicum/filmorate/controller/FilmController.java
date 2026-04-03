package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.InMemoryStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final InMemoryStore store;

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        normalizeFilm(film, null);
        film.setId(store.nextFilmId());
        store.getFilms().put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        Film existing = getFilmOrThrow(film.getId());
        normalizeFilm(film, existing);
        store.getFilms().put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(store.getFilms().values());
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Integer id) {
        return getFilmOrThrow(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        Film film = getFilmOrThrow(id);
        if (!store.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        Film film = getFilmOrThrow(id);
        if (!store.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        return store.getFilms().values().stream()
                .sorted(Comparator
                        .comparingInt((Film film) -> film.getLikes().size()).reversed()
                        .thenComparing(Film::getId))
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(Integer id) {
        Film film = store.getFilms().get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    private void normalizeFilm(Film film, Film existing) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            film.setMpa(store.getMpaMap().get(1));
        } else {
            Mpa mpa = store.getMpaMap().get(film.getMpa().getId());
            if (mpa == null) {
                throw new ValidationException("Некорректный рейтинг MPA");
            }
            film.setMpa(mpa);
        }
        film.setGenres(store.normalizeGenres(film.getGenres()));
        film.setDirectors(store.normalizeDirectors(film.getDirectors()));
        if (existing != null) {
            film.setLikes(existing.getLikes());
        }
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания фильма — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
