package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> getAllFilms() { return service.getAll(); }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) { return service.getById(id); }

    @PostMapping
    public Film addFilm(@RequestBody Film film) { return service.add(film); }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) { return service.update(film); }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id) { service.delete(id); }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) { service.addLike(id, userId); }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) { service.removeLike(id, userId); }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        return service.getPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return service.getByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam(required = false) String by) {
        return service.search(query, by);
    }
}
