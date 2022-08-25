package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int id);

    Optional<Review> getReviewById(int id);

    List<Review> getReviews();

    void addLikeToReview(int reviewId, int userId);

    void addDislikeToReview(int reviewId, int userId);

    void deleteLikeOrDislike(int reviewId, int userId);

    List<Review> getReviewsOfFilm(int filmId, int count);

    int getUsefulOfReview(int reviewId);
}
