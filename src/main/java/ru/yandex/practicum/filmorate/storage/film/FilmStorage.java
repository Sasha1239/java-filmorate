package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {
    Film create(@Valid @RequestBody Film film);

    Film update(@Valid @RequestBody Film film);

    List<Film> getAll();

    Film getFilm(int idFilm);
}
