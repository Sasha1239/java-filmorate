package ru.yandex.practicum.filmorate.service.review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReviewService {

    ReviewStorage reviewStorage;

    FeedStorage feedStorage;

    public Review createReview(Review review) {
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неверный запрос, проверьте идентификаторы фильма и пользователя");
        }
        Review createdReview = reviewStorage.createReview(review);
        if (createdReview != null) {
            feedStorage.feed(createdReview.getUserId(), createdReview.getReviewId(), Event.REVIEW, Operation.ADD);
        }
        return createdReview;
    }

    public Review updateReview(Review review) {
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неверный запрос, проверьте идентификаторы фильма и пользователя");
        }
        Optional<Review> updatingReview = reviewStorage.getReviewById(review.getReviewId());
        Review updatedReview = reviewStorage.updateReview(review);
        if (updatedReview != null && updatingReview.isPresent()) {
            feedStorage.feed(updatingReview.get().getUserId(), updatingReview.get().getReviewId(),
                    Event.REVIEW, Operation.UPDATE);
        }
        return updatedReview;
    }

    public void deleteReview(int id) {
        Optional<Review> deletedReview = reviewStorage.getReviewById(id);
        reviewStorage.deleteReview(id);
        deletedReview.ifPresent(review -> feedStorage.feed(review.getUserId(), review.getReviewId(), Event.REVIEW,
                Operation.REMOVE));
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
