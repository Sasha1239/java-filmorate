package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //Добавление фильма
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    //Обновление фильма
    @PutMapping
    public Optional<Film> update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    //Получение всех фильмов
    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    //Получение определенного фильма
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    //Удаление фильма по идентификатору
    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable int filmId) {
        filmService.removeFilm(filmId);
    }

    //Пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.addLikeFilm(id, userId);
    }

    //Пользователь удаляет лайк
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLikeFilm(id, userId);
    }

    //Получение самых популярных фильмов по кол-ву лайков, отфильтрованных по жанру и году
    // или получение первых 10 фильмов
    @GetMapping("/popular")
    public List<Film> getPopularFilm(@Positive @RequestParam(defaultValue = "10") int count,
                                     @RequestParam(required = false) Integer genreId,
                                     @RequestParam(required = false) Integer year) {

        log.info("getPopularFilm (GET /films/popular?count={}&genreId={}&year={}): Получить список из первых {} " +
                "фильмов по количеству лайков c фильтрацией по жанру (если 0, то без фильтрации по жанру) {} " +
                "и по году (если 0, то без фильтрации по жанру) {}", count, genreId, year, count, genreId, year);
        List<Film> films = filmService.getPopularFilm(count, genreId, year);
        log.info("getPopularFilm (GET /films/popular): Результат = {}", films);
        return films;
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getAllFilmsOfDirector(@PathVariable int directorId,
                                            @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.getAllFilmsOfDirector(directorId, sortBy);
    }

    //Поиск по названию фильма и/или по режиссёру. Возвращает список фильмов, отсортированных по популярности.
    @GetMapping("/search")
    public List<Film> searchFilmsByNameByDirector(@RequestParam(required = false) String query,
                                                  @RequestParam(required = false) String by) {

        log.info("searchFilmsByNameByDirector (GET /films/search?query={}&by={}):  ", query, by);
        List<Film> films = filmService.searchFilmsByNameByDirector(query, by);
        log.info("searchFilmsByNameByDirector (GET /films/search?query={}&by={}): Результат = {}", query, by, films);
        return films;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms (@RequestParam int userId, @RequestParam int friendId) {
        log.info("getCommonFilms (GET /common?userId={}&friendId={}):",userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

}