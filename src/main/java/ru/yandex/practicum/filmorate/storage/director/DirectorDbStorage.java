package ru.yandex.practicum.filmorate.storage.director;

import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director getDirectorById(long id) {
        if (id>0){
            String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
            if (sqlRowSet.next()) {
                Director director = Director.builder()
                        .id(sqlRowSet.getLong("director_id"))
                        .name(sqlRowSet.getString("director_name"))
                        .build();
                return director;
            } else {
                throw new NotFoundException("Режисер не найден");
            }
        } else {
            return Director.builder().id(0).name("Не указан").build();
        }
    }

    @Override
    public List<Director> getDirectorList() {
        String sql = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sql, this::mapToRowDirector);
    }

    @Override
    public void addDirectorToFilm(long film_id, Set<Director> directors) {
        String sql = "MERGE INTO FILM_DIRECTOR (DIRECTOR_ID, FILM_ID) " +
                "VALUES (?,?)";
        for (Director director: directors){
            jdbcTemplate.update(sql, director.getId(), film_id);
        }
    }

    @Override
    public void removeDirectorToFilm(long film_id) {
        String sql = "DELETE FROM FILM_DIRECTOR " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sql, film_id);
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO DIRECTORS (DIRECTOR_NAME) " +
                "VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    new String[]{"DIRECTOR_ID"});
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? " +
                "WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Director> getAllDirectorsOfFilm(long filmId) {
        String sql = "SELECT * FROM FILM_DIRECTOR FD " +
                "LEFT JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE FILM_ID = ? ";
        return jdbcTemplate.query(sql, this::mapToRowDirector, filmId);
    }

    private Director mapToRowDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
