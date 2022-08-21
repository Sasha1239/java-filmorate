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

import ru.yandex.practicum.filmorate.exception.ValidationException;
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

        if (film.getDirectors() != null) {
            directorStorage.addDirectorToFilm(film.getId(), film.getDirectors());
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
        directorStorage.removeDirectorToFilm(film.getId());
        if (film.getDirectors() != null) {
            directorStorage.addDirectorToFilm(film.getId(), film.getDirectors());
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
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        StringBuilder getPopularFilmsSql = new StringBuilder();
        getPopularFilmsSql.append(
                "SELECT * "+
                "FROM FILM f " +
                "JOIN MPA m ON (m.mpa_rating_id = f.mpa_rating) " +
                "LEFT JOIN " +
                        "(SELECT film_id, COUNT(user_id) as rate " +
                        "FROM FILM_LIKES " +
                        "GROUP BY film_id) fl ON (fl.film_id = f.film_id) ");
       if (genreId != null) {
           getPopularFilmsSql.append(
                "JOIN FILM_GENRE g ON (g.film_id = f.film_id AND g.genre_id = " + genreId + ") ");
       }
        if (year != null) {
            getPopularFilmsSql.append(
                "WHERE EXTRACT(YEAR from CAST(f.release_date AS DATE)) = "+ year +" ");
        }
        getPopularFilmsSql.append(
                "ORDER BY fl.rate DESC " +
                "LIMIT ?");
        return jdbcTemplate.query(getPopularFilmsSql.toString(), this::makeFilm, count);
    }

    @Override
    public List<Film> getAllFilmOfDirector(int directorId, String sortBy) throws ValidationException {
        if (sortBy.equals("year")) {
            String sql = "SELECT * " +
                    "FROM FILM F " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                    "LEFT JOIN MPA M on M.MPA_RATING_ID = F.MPA_RATING " +
                    "LEFT JOIN FILM_GENRE FG on F.FILM_ID = FG.FILM_ID " +
                    "WHERE FD.DIRECTOR_ID = ? " +
                    "GROUP BY EXTRACT(YEAR from CAST(RELEASE_DATE AS DATE))";
            return jdbcTemplate.query(sql, this::makeFilm, directorId);
        } else if (sortBy.equals("likes")) {
            String sql = "SELECT * " +
                    "FROM FILM F " +
                    "LEFT JOIN FILM_GENRE FG on F.FILM_ID = FG.FILM_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                    "LEFT JOIN MPA M on M.MPA_RATING_ID = F.MPA_RATING " +
                    "LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID " +
                    "WHERE FD.DIRECTOR_ID = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(FL.FILM_ID)";
            return jdbcTemplate.query(sql, this::makeFilm, directorId);
        } else {
            throw new ValidationException("Неверно указан параметр запроса");
        }
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
        Set<Director> directorList = new HashSet<>(directorStorage
                .getAllDirectorsOfFilm(resultSet.getInt("film_id")));
        return new Film(idFilm, nameFilm, descriptionFilm, releaseDateFilm, durationFilm, mpaRatingFilm, genres, directorList);
    }


    public List<Integer> getUserLikedFilms(int idUser) {
        return jdbcTemplate.query("SELECT film_id FROM film_likes WHERE user_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("film_id"), idUser);
    }

    @Override

    public List<Film> searchFilmsByNameByDirector(String searchStr, String searchBy) {
        StringBuilder searchFilmsByNameByDirectorSql = new StringBuilder();
        searchFilmsByNameByDirectorSql.append(
                            "SELECT DISTINCT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                                    "m.mpa_rating_id, m.mpa_name, fl.rate "+
                            "FROM FILM f " +
                            "JOIN MPA m ON (m.mpa_rating_id = f.mpa_rating) " +
                            "LEFT JOIN " +
                                    "(SELECT film_id, COUNT(user_id) as rate " +
                                    "FROM FILM_LIKES " +
                                    "GROUP BY film_id) fl ON (fl.film_id = f.film_id) " +
                            "LEFT JOIN FILM_DIRECTOR fd ON (fd.film_id = f.film_id)" +
                            "LEFT JOIN DIRECTORS d ON (fd.director_id = d.director_id) ");

        if (searchStr != null && searchBy != null) {
            if (searchBy.toUpperCase().contains("TITLE") || searchBy.toUpperCase().contains("DIRECTOR")) {
                searchFilmsByNameByDirectorSql.append(
                            "WHERE ");
                if (searchBy.toUpperCase().contains("TITLE")) {
                    searchFilmsByNameByDirectorSql.append(
                            "UPPER(f.film_name) like '%" + searchStr.toUpperCase() + "%' ");
                }
                if (searchBy.toUpperCase().contains("DIRECTOR")) {
                    if (searchBy.toUpperCase().contains("TITLE")) {
                        searchFilmsByNameByDirectorSql.append(
                             " OR ");
                    }
                    searchFilmsByNameByDirectorSql.append(
                            "UPPER(d.director_name) like '%" + searchStr.toUpperCase() + "%' ");
                }
            }
        }
        searchFilmsByNameByDirectorSql.append(
                            "ORDER BY fl.rate DESC ");
        return jdbcTemplate.query(searchFilmsByNameByDirectorSql.toString(), this::makeFilm);

    }

}
