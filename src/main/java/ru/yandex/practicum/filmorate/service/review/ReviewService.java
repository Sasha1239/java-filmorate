package ru.yandex.practicum.filmorate.service.review;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review createReview(Review review) {
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неверный запрос, проверьте идентификаторы фильма и пользователя");
        }
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неверный запрос, проверьте идентификаторы фильма и пользователя");
        }
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(int id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id).orElseThrow(() -> new NotFoundException("Обзор с таким id не найден"));
    }

    public List<Review> getReviews() {
        return reviewStorage.getReviews();
    }

    public List<Review> getReviewsOfFilm(int filmId, int count) {
        return reviewStorage.getReviewsOfFilm(filmId, count);
    }

    public void addLikeToReview(int userId, int reviewId) {
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(int userId, int reviewId) {
        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    public void deleteLikeOrDislike (int reviewId, int userId) {
        reviewStorage.deleteLikeOrDislike(reviewId, userId);
    }
}
