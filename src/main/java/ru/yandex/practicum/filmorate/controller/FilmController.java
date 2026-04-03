package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirector(
            @PathVariable Integer directorId,
            @RequestParam String sortBy
    ) {
        return filmService.getByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(
            @RequestParam String query,
            @RequestParam String by
    ) {
        return filmService.search(query, by);
    }
}
