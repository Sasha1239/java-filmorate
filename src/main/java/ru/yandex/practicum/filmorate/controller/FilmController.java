package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService){
        this.filmService = filmService;
    }

    //Добавление фильма
    @PostMapping
    public Film create(@Valid @RequestBody Film film){
        return filmService.create(film);
    }

    //Обновление фильма
    @PutMapping
    public Optional<Film> update(@Valid @RequestBody Film film){
        return filmService.update(film);
    }

    //Получение всех фильмов
    @GetMapping
    public List<Film> getAll(){
        return filmService.getAll();
    }

    //Получение определенного фильма
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id){
        return filmService.getFilm(id);
    }

    //Удаление фильма по идентификатору
    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable int filmId){
        filmService.removeFilm(filmId);
    }

    //Пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable int id, @PathVariable int userId){
        filmService.addLikeFilm(id, userId);
    }

    //Пользователь удаляет лайк
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFilm(@PathVariable int id, @PathVariable int userId){
        filmService.removeLikeFilm(id, userId);
    }

    //Получение самых популярных фильмов по кол-ву лайков или получение первых 10 фильмов
    @GetMapping("/popular")
    public List<Film> getPopularFilm(@Positive @RequestParam(defaultValue = "10") int count){
        return filmService.getPopularFilm(count);
    }
}