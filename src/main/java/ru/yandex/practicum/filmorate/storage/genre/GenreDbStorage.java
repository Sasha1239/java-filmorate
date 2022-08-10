package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Получение жанра по идентификатору
    @Override
    public Optional<Genre> getGenre(int idGenre) {
        final String getGenreSql = "SELECT * FROM GENRE WHERE GENRE_ID = ?";

        return jdbcTemplate.query(getGenreSql, this:: makeGenre, idGenre).stream().findAny();
    }

    //Получение жанра по фильму
    @Override
    public List<Genre> getGenresFilm(int idFilm) {
        final String getGenresFilm = "SELECT * FROM GENRE " +
                "LEFT JOIN FILM_GENRE FG ON GENRE.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID = ?";

        //return jdbcTemplate.query(getGenresFilm, this::makeGenre, idFilm);
        return jdbcTemplate.query(getGenresFilm, this::makeGenre ,idFilm);
    }

    //Получение всех жанров
    @Override
    public List<Genre> getAllGenres() {
        final String getAllGenresSql = "SELECT * FROM GENRE";

        return jdbcTemplate.query(getAllGenresSql, this::makeGenre);
    }

    //Добавление жанра к фильму
    @Override
    public void addGenreToFilm(int idFilm, List<Genre> genres) {
        final String addGenreFilm = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";

        for (Genre genre : genres) {
            genre.setName(getGenre(genre.getId()).get().getName());
            jdbcTemplate.update(addGenreFilm, idFilm, genre.getId());
        }
    }

    //Удаление жанра из фильма
    @Override
    public void removeGenreToFilm(int idFilm) {
        final String removeGenreFilmSql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";

        jdbcTemplate.update(removeGenreFilmSql, idFilm);
    }

    private Genre makeGenre(ResultSet resultSet, int RowNow) throws SQLException {
        int idGenre = resultSet.getInt("GENRE_ID");
        String nameGenre = resultSet.getString("GENRE_NAME");
        return new Genre(idGenre, nameGenre);
    }
}
