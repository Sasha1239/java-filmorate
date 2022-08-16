package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private final DirectorStorage directorStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    //Добавление фильма
    @Override
    public Film create(Film film) {
        final String createFilmSql = "INSERT INTO FILM (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, " +
                "MPA_RATING) VALUES (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(createFilmSql,
                    new String[]{"FILM_ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        int filmId = (Objects.requireNonNull(keyHolder.getKey())).intValue();
        film.setId(filmId);

        if (film.getGenres() != null) {
            List<Genre> genres = removeGenreDuplicate(film);
            genreStorage.addGenreToFilm(filmId, genres);
        }

        if (film.getDirector() != null) {
            directorStorage.addDirectorToFilm(film.getId(), film.getDirector().getId());
        }
        return film;
    }


    //Обновление фильма
    @Override
    public Optional<Film> update(Film film) {
        final String updateFilmSql = "UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_RATING = ? " +
                "WHERE FILM_ID = ?;";

        jdbcTemplate.update(updateFilmSql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        if (film.getGenres() != null) {
            List<Genre> genres = removeGenreDuplicate(film);
            genreStorage.removeGenreToFilm(film.getId());
            genreStorage.addGenreToFilm(film.getId(), genres);
        }

        if (film.getDirector() != null) {
            directorStorage.addDirectorToFilm(film.getId(), film.getDirector().getId());
        }
        return getFilm(film.getId());
    }

    //Получение всех фильмов
    @Override
    public List<Film> getAll() {
        final String getAllFilmSql = "SELECT * FROM FILM AS F " +
                "LEFT JOIN MPA M ON M.MPA_RATING_ID = F.MPA_RATING;";

        return jdbcTemplate.query(getAllFilmSql, this::makeFilm);
    }

    //Получение фильма по идентификатору
    @Override
    public Optional<Film> getFilm(int idFilm) {
        final String getFilmSql = "SELECT * FROM FILM AS F " +
                "LEFT JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
                "LEFT JOIN MPA M ON M.MPA_RATING_ID = F.MPA_RATING " +
                "LEFT JOIN GENRE G ON G.GENRE_ID = FG.GENRE_ID " +
                "LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID " +
                "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE F.FILM_ID = ?;";

        return jdbcTemplate.query(getFilmSql, this::makeFilm, idFilm).stream().findAny();
    }

    @Override
    public void removeFilm(int idFilm) {
        final String removeFilmSql = "DELETE FROM FILM WHERE FILM_ID = ?;";

        jdbcTemplate.update(removeFilmSql, idFilm);
    }

    @Override
    public void addLikeFilm(int idFilm, int idUser) {
        final String addLikeFilmSql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?);";

        jdbcTemplate.update(addLikeFilmSql, idFilm, idUser);
    }

    @Override
    public void removeLikeFilm(int idFilm, int idUser) {
        final String removeLikeFilm = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?;";

        jdbcTemplate.update(removeLikeFilm, idFilm, idUser);
    }

    @Override
    public List<Integer> getLike(int idFilm) {
        final String getLikeSql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?;";

        return jdbcTemplate.queryForList(getLikeSql, Integer.class, idFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        final String getPopularFilmsSql = "SELECT * FROM FILM AS F " +
                "LEFT JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
                "LEFT JOIN MPA M ON M.MPA_RATING_ID = F.MPA_RATING " +
                "LEFT JOIN GENRE G ON G.GENRE_ID = FG.GENRE_ID " +
                "LEFT OUTER JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID " +
                "GROUP BY FL.USER_ID, F.FILM_ID, FG.GENRE_ID " +
                "ORDER BY COUNT(FL.FILM_ID) " +
                "DESC LIMIT ?;";

        return jdbcTemplate.query(getPopularFilmsSql, this::makeFilm, count);
    }

    private List<Genre> removeGenreDuplicate(Film film) {
        film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));
        return film.getGenres();
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int idFilm = resultSet.getInt("FILM_ID");
        String nameFilm = resultSet.getString("FILM_NAME");
        String descriptionFilm = resultSet.getString("DESCRIPTION");
        LocalDate releaseDateFilm = LocalDate.parse(resultSet.getString("RELEASE_DATE"));
        int durationFilm = resultSet.getInt("DURATION");
        Mpa mpaRatingFilm = new Mpa(resultSet.getInt("MPA_RATING_ID"),
                resultSet.getString("MPA_NAME"));
        List<Genre> genres = genreStorage.getGenresFilm(idFilm);
        Director director = directorStorage.getDirectorById(resultSet.getLong("director_id"));
        return new Film(idFilm, nameFilm, descriptionFilm, releaseDateFilm, durationFilm, mpaRatingFilm, genres, director);
    }
}