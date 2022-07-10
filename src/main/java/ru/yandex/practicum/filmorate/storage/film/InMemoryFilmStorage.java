package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films= new HashMap<>();
    private int idFilm;

    //Добавление фильма
    public Film create(Film film){
        film.setId(++idFilm);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    //Обновление фильма
    public Film update(Film film){
        films.put(film.getId(), film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    //Получение всех фильмов
    public List<Film> getAll(){
        return new ArrayList<>(films.values());
    }

    //Получение фильма по идентификатору
    public Optional<Film> getFilm (int idFilm){
        return Optional.ofNullable(films.get(idFilm));
    }
}
