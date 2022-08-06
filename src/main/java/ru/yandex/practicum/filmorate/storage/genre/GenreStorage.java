package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Optional<Genre> getGenre(int idGenre);
    List<Genre> getGenresFilm(int idGenre);
    List<Genre> getAllGenres();
    void addGenreToFilm(int idFilm, List<Genre> genres);
    void removeGenreToFilm(int idFilm);
}
