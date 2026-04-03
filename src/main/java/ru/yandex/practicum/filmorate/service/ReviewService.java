package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.InMemoryDataStore;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final InMemoryDataStore store;
    private final UserService userService;
    private final FilmService filmService;

    public ReviewService(InMemoryDataStore store, UserService userService, FilmService filmService) {
        this.store = store;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Review add(Review review) {
        validate(review);
        userService.getById(review.getUserId());
        filmService.getById(review.getFilmId());
        review.setReviewId(store.nextReviewId());
        if (review.getUseful() == null) review.setUseful(0);
        if (review.getLikedBy() == null) review.setLikedBy(new java.util.HashSet<>());
        if (review.getDislikedBy() == null) review.setDislikedBy(new java.util.HashSet<>());
        store.reviews.put(review.getReviewId(), review);
        userService.addEvent(review.getUserId(), review.getReviewId(), "REVIEW", "ADD");
        return review;
    }

    public Review update(Review review) {
        if (review.getReviewId() == null) {
            throw new ValidationException("ID отзыва должен быть указан");
        }
        Review existing = getById(review.getReviewId());
        validate(review);
        userService.getById(review.getUserId());
        filmService.getById(review.getFilmId());
        review.setLikedBy(existing.getLikedBy());
        review.setDislikedBy(existing.getDislikedBy());
        review.setUseful(existing.getUseful());
        store.reviews.put(review.getReviewId(), review);
        userService.addEvent(review.getUserId(), review.getReviewId(), "REVIEW", "UPDATE");
        return review;
    }

    public Review getById(Integer id) {
        Review review = store.reviews.get(id);
        if (review == null) {
            throw new NotFoundException("Отзыв с id " + id + " не найден");
        }
        return review;
    }

    public void delete(Integer id) {
        Review review = getById(id);
        store.reviews.remove(id);
        userService.addEvent(review.getUserId(), id, "REVIEW", "REMOVE");
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        int limit = count == null ? 10 : count;
        return store.reviews.values().stream()
                .filter(r -> filmId == null || Objects.equals(r.getFilmId(), filmId))
                .sorted(Comparator.comparingInt(Review::getUseful).reversed().thenComparing(Review::getReviewId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void addLike(Integer reviewId, Integer userId) {
        Review review = getById(reviewId);
        userService.getById(userId);
        if (review.getLikedBy().add(userId)) {
            review.getDislikedBy().remove(userId);
            recalcUseful(review);
        }
    }

    public void addDislike(Integer reviewId, Integer userId) {
        Review review = getById(reviewId);
        userService.getById(userId);
        if (review.getDislikedBy().add(userId)) {
            review.getLikedBy().remove(userId);
            recalcUseful(review);
        }
    }

    public void removeLike(Integer reviewId, Integer userId) {
        Review review = getById(reviewId);
        userService.getById(userId);
        review.getLikedBy().remove(userId);
        recalcUseful(review);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        Review review = getById(reviewId);
        userService.getById(userId);
        review.getDislikedBy().remove(userId);
        recalcUseful(review);
    }

    private void recalcUseful(Review review) {
        review.setUseful(review.getLikedBy().size() - review.getDislikedBy().size());
    }

    private void validate(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Текст отзыва не может быть пустым");
        }
        if (review.getUserId() == null || review.getFilmId() == null || review.getIsPositive() == null) {
            throw new ValidationException("Поля userId, filmId и isPositive обязательны");
        }
    }
}
