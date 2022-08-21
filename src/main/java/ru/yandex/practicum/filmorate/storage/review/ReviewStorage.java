package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review createReview (Review review);

    Review updateReview (Review review);

    public void deleteReview (int id);

    public Optional<Review> getReviewById (int id);

    public List<Review> getReviews();

    public void addLikeToReview(int reviewId, int userId);

    public void addDislikeToReview(int reviewId, int userId);

    public List<Review> getReviewsOfFilm(int filmId, int count);

    public int getUsefulOfReview(int reviewId);
}
