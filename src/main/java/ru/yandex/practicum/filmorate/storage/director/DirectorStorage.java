package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    Director getDirectorById (long id);

    List<Director> getDirectorList ();

    void addDirectorToFilm (long film_id, long director_id);

    void removeDirectorToFilm (long film_id, long director_id);

    void createDirector (Director director);

    void updateDirector (Director director);
}
