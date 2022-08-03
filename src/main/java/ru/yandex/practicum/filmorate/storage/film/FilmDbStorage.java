package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    //Добавление фильма
    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(FilmSql.CREATE_FILM,
                    new String[]{"FILM_ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpaRating().getId());
            return preparedStatement;
        }, keyHolder);
        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        if (film.getGenre() != null) {
            List<Genre> genres = removeGenreDuplicate(film);
            genreStorage.addGenreToFilm(filmId, genres);
        }
        return film;
    }

    //Обновление фильма
    @Override
    public Film update(Film film) {
        //films.put(film.getId(), film);
        //log.info("Обновлен фильм {}", film);
        jdbcTemplate.update(FilmSql.UPDATE_FILM, film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpaRating().getId());

        if (film.getGenre() != null) {
            List<Genre> genres = removeGenreDuplicate(film);
            genreStorage.removeGenreToFilm(film.getId());
            genreStorage.addGenreToFilm(film.getId(), genres);
        }

        return film;
    }

    //Получение всех фильмов
    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query(FilmSql.GET_ALL_FILMS, this::makeFilm);
    }

    //Получение фильма по идентификатору
    @Override
    public Optional<Film> getFilm(int idFilm) {
        return jdbcTemplate.query(FilmSql.GET_FILM_ID, this::makeFilm, idFilm).stream().findAny();
        //return Optional.ofNullable(films.get(idFilm));
    }

    @Override
    public void removeFilm(int idFilm) {
        jdbcTemplate.update(FilmSql.REMOVE_FILM, idFilm);
    }

    @Override
    public void addLikeFilm(int idFilm, int idUser) {
        jdbcTemplate.update(FilmSql.LIKE_FILM, idFilm, idUser);
    }

    @Override
    public void removeLikeFilm(int idFilm, int idUser) {
        jdbcTemplate.update(FilmSql.REMOVE_LIKE, idFilm, idUser);
    }

    @Override
    public List<Integer> getLike(int idFilm) {
        return jdbcTemplate.queryForList(FilmSql.GET_LIKES, Integer.class, idFilm);
    }

    @Override
    public List<Optional<Film>> getPopularFilms(int count) {
        List<Integer> idFilms = jdbcTemplate.queryForList(FilmSql.GET_POPULAR_FILMS, Integer.class, count);
        return idFilms.stream().map(this::getFilm).collect(Collectors.toList());
    }

    private List<Genre> removeGenreDuplicate(Film film) {
        film.setGenre(film.getGenre().stream().distinct().collect(Collectors.toList()));
        return film.getGenre();
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int idFilm = resultSet.getInt("FILM_ID");
        String nameFilm = resultSet.getString("FILM_NAME");
        String descriptionFilm = resultSet.getString("DESCRIPTION");
        //Date releaseDateFilm = Date.valueOf(resultSet.getDate("RELEASE_DATE").toLocalDate());
        LocalDate releaseDateFilm = LocalDate.parse(resultSet.getString("RELEASE_DATE"));
        int durationFilm = resultSet.getInt("DURATION");
        Mpa mpaRatingFilm = new Mpa(resultSet.getInt("MPA_RATING_ID"),
                resultSet.getString("MPA_NAME"));
        List<Genre> genres = genreStorage.getGenresFilm(idFilm);
        return new Film(idFilm, nameFilm, descriptionFilm, releaseDateFilm, durationFilm, mpaRatingFilm, genres);
    }
}
