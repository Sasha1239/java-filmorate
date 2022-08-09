package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Optional<Film> update(Film film);

    List<Film> getAll();

    Optional<Film> getFilm(int idFilm);

    void removeFilm(int idFilm);

    void addLikeFilm(int idFilm, int idUser);

    void removeLikeFilm(int idFilm, int idUser);

    List<Integer> getLike(int idFilm);

    List<Film> getPopularFilms(int count);
}
