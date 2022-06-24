package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films= new HashMap<>();
    private int idFilm;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MAX_EARLY_DATE_FILM = LocalDate.of(1895, 12, 28);

    //Добавление фильма
    @PostMapping
    public Film create(@Valid @RequestBody Film film){
        validateFilm(film);
        film.setId(++idFilm);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    //Обновление фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film film){
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Попробуйте другой идентификатор фильма");
        }

        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    //Получение всех фильмов
    @GetMapping
    public List<Film> getAll(){
        return new ArrayList<>(films.values());
    }

    //Валидация
    private void validateFilm(Film film){
        try {
            if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            if (film.getReleaseDate().isBefore(MAX_EARLY_DATE_FILM)){
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        } catch (ValidationException e){
            log.error("ValidationException", e);
            throw e;
        }
    }
}