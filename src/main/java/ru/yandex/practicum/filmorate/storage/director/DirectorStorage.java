package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    Director getDirectorById (long id);

    List<Director> getDirectorList ();

    void addDirectorToFilm (long film_id, Set<Director> directors);

    void removeDirectorToFilm (long film_id);

    Director createDirector (Director director);

    Director updateDirector (Director director);

    void deleteDirector (long id);

    List<Director> getAllDirectorsOfFilm (long filmId);
}
