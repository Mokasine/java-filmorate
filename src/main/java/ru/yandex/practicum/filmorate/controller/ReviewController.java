package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Review> getAll(@RequestParam(required = false) Integer filmId,
                               @RequestParam(required = false) Integer count) {
        return service.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Integer id) { return service.getById(id); }

    @PostMapping
    public Review add(@RequestBody Review review) { return service.add(review); }

    @PutMapping
    public Review update(@RequestBody Review review) { return service.update(review); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { service.delete(id); }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) { service.addLike(id, userId); }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) { service.addDislike(id, userId); }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) { service.removeLike(id, userId); }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) { service.removeDislike(id, userId); }
}
