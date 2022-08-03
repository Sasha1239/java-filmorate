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

    @Override
    public Optional<Genre> getGenre(int idGenre) {
        return jdbcTemplate.query(GenreSql.GET_GENRE, this::makeGenre, idGenre).stream().findAny();
    }

    @Override
    public List<Genre> getGenresFilm(int idFilm) {
        return jdbcTemplate.query(GenreSql.GET_GENRE_TO_FILM, this::makeGenre, idFilm);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(GenreSql.GET_GENRE, this::makeGenre);
    }

    @Override
    public void addGenreToFilm(int idFilm, List<Genre> genres) {
        for (Genre genre : genres) {
            genre.setName(getGenre(genre.getId()).get().getName());
            jdbcTemplate.update(GenreSql.ADD_GENRE_TO_FILM, idFilm, genre.getId());
        }
    }

    @Override
    public void removeGenreToFilm(int idFilm) {
        jdbcTemplate.update(GenreSql.REMOVE_GENRE_TO_FILM, idFilm);
    }

    private Genre makeGenre(ResultSet resultSet, int RowNow) throws SQLException {
        int idGenre = resultSet.getInt("genre_id");
        String nameGenre = resultSet.getString("genre_name");
        return new Genre(idGenre, nameGenre);
    }
}
