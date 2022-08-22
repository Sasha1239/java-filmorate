package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
    }

    @GetMapping(value = "{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam (defaultValue = "0") int filmId,
                                   @RequestParam (defaultValue = "10") int count) {
        if (filmId == 0) {
            return reviewService.getReviews();
        } else {
            return reviewService.getReviewsOfFilm(filmId, count);
        }
    }

    @PutMapping (value = "/{reviewId}/like/{userId}")
    public void addLikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.addLikeToReview(userId, reviewId);
    }

    @PutMapping(value = "/{reviewId}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.addDislikeToReview(userId, reviewId);
    }

    @DeleteMapping (value = "/{reviewId}/like/{userId}")
    public void deleteLikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.deleteLikeOrDislike(userId, reviewId);
    }

    @DeleteMapping(value = "/{reviewId}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.deleteLikeOrDislike(userId, reviewId);
    }
}
