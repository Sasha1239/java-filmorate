package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest extends FilmorateApplicationTests{
    private final FilmController filmController = new FilmController();

    //Наименование фильма не заполнено
    @Test
    public void addFilmNameEmpty(){
        Film film = new Film();
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");
        filmController.create(film);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();
        assertEquals("Наименование фильма не может быть пустым", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Длина описания фильма больше 200 символов
    @Test
    public void addFilmDescriptionMorePossibleCountSymbol(){
        String description = "а".repeat(201);
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDescription(description);

        Throwable throwable = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals("Максимальная длина описания — 200 символов", throwable.getMessage(),
                "Текст ошибки валидации разный");

    }

    //Дата релиза раньше чем 28 декабря 1895 года
    @Test
    public void addFilmReleaseDateEarlyPossibleDate(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.of(1800, 12, 12));

        Throwable throwable = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Продолжительность фильма отрицательная
    @Test
    public void addFilmDurationLessPossible(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-30);
        filmController.create(film);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();
        assertEquals("Продолжительность фильма должна быть положительной", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Обновление данных фильма
    @Test
    public void updateFilmData(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        Film film1 = filmController.update(new Film(1, "Наименование", "Описание",
                LocalDate.of(1991, 12, 4), 45));

        assertNotEquals(film, film1, "Данные фильма совпадают");
    }

    //Получение всех фльмов
    @Test
    public void getAllFilms(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        Film film1 = new Film();
        film1.setName("Наименование фильма1");
        film1.setDescription("Описание фильма1");
        film1.setDuration(50);
        film1.setReleaseDate(LocalDate.of(1995, 2, 12));
        filmController.create(film1);

        List<Film> filmList = filmController.getAll();

        assertEquals(filmList.size(), 2, "Количество фильмов не совпадают");
    }
}
