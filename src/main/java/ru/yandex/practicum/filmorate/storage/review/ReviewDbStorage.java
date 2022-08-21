package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review createReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO REVIEW (CONTENT, USER_ID, FILM_ID, IS_POSITIVE) " +
                "VALUES ( ?, ?, ?, ? )";
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"REVIEW_ID"});
            preparedStatement.setString(1, review.getContent());
            preparedStatement.setInt(2, userStorage.getUser(review.getUserId())
                    .orElseThrow(() -> new NotFoundException("Пользовтель не найден")).getId());
            preparedStatement.setInt(3, filmStorage.getFilm(review.getFilmId())
                    .orElseThrow(() -> new NotFoundException("Фильм не найден")).getId());
            preparedStatement.setBoolean(4, review.getIsPositive());
            return preparedStatement;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE REVIEW " +
                "SET CONTENT = ?, " +
                "IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(int id) {
        String sql = "DELETE FROM REVIEW " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        String sql = "SELECT * " +
                "FROM REVIEW " +
                "WHERE REVIEW_ID = ?";
        return jdbcTemplate.query(sql, this::mapToRowReview, id).stream().findAny();
    }

    @Override
    public List<Review> getReviews() {
        String sql = "SELECT * FROM REVIEW";
        return jdbcTemplate.query(sql, this::mapToRowReview).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getReviewsOfFilm(int filmId, int count) {
        String sql = "SELECT * " +
                "FROM REVIEW " +
                "LEFT JOIN FILM F on F.FILM_ID = REVIEW.FILM_ID " +
                "WHERE F.FILM_ID = ? " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapToRowReview, filmId, count).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void addLikeToReview(int reviewId, int userId) {
        String sql = "INSERT INTO REVIEW_LIKE (REVIEW_ID, USER_ID, IS_USEFUL)" +
                "VALUES ( ?, ?, ? ) ";
        jdbcTemplate.update(sql, reviewId, userId, true);
    }

    @Override
    public void addDislikeToReview(int reviewId, int userId) {
        String sql = "INSERT INTO REVIEW_LIKE (REVIEW_ID, USER_ID, IS_USEFUL)" +
                "VALUES ( ?, ?, ? ) ";
        jdbcTemplate.update(sql, reviewId, userId, false);
    }

    @Override
    public int getUsefulOfReview(int reviewId) {
        int useful = 0;
        String sql = "SELECT * " +
                "FROM REVIEW_LIKE " +
                "WHERE REVIEW_ID = ?";
        List<ReviewLike> reviewLikeList = jdbcTemplate.query(sql, this::mapToRowReviewLike, reviewId);
        for (ReviewLike reviewLike: reviewLikeList){
            if (reviewLike.isUseful()){
                useful+=1;
            } else {
                useful-=1;
            }
        }
        return useful;
    }

        private Review mapToRowReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(getUsefulOfReview(rs.getInt("review_id")))
                .build();
    }

    private ReviewLike mapToRowReviewLike(ResultSet rs, int rowNum) throws SQLException {
        return ReviewLike.builder()
                .reviewId(rs.getInt("review_id"))
                .userId(rs.getInt("user_id"))
                .isUseful(rs.getBoolean("is_useful"))
                .build();
    }
}
