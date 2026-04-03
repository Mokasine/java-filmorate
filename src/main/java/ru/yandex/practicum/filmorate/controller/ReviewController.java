package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getAll(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        return reviewService.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Integer id) {
        return reviewService.getById(id);
    }

    @PostMapping
    public Review add(@RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeDislike(id, userId);
    }
}
