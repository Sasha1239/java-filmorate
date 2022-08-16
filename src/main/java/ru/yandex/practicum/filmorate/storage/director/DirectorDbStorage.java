package ru.yandex.practicum.filmorate.storage.director;

import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public void addDirectorToFilm(long film_id, long director_id) {
        String sql = "MERGE INTO FILM_DIRECTOR (DIRECTOR_ID, FILM_ID) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql, director_id, film_id);
    }

    @Override
    public void removeDirectorToFilm(long film_id, long director_id) {
        String sql = "DELETE FROM FILM_DIRECTOR " +
                "where DIRECTOR_ID = ? and  FILM_ID = ?";
        jdbcTemplate.update(sql, director_id, film_id);
    }

    @Override
    public void createDirector(Director director) {
        String sql = "INSERT INTO DIRECTORS (DIRECTOR_NAME) " +
                "VALUES (?)";
        jdbcTemplate.update(sql, director.getName());
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? " +
                "WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
    }

    private Director mapToRowDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
