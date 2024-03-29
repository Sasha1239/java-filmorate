package ru.yandex.practicum.filmorate.controllerTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest extends FilmorateApplicationTests {
    private final FilmController filmController;
    private final UserController userController;
    private final FilmDbStorage filmDbStorage;

    private final List<Genre> genres = new ArrayList<>();

    //Наименование фильма не заполнено
    @Test
    public void addFilmNameEmpty() {
        Film film = new Film();
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");

        Throwable throwable = assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("create.film.name: Наименование фильма не может быть пустым или содержать только пробельные символы",
                throwable.getMessage(), "Текст ошибки валидации разный");
    }

    //В наименование фильма передается null
    @Test
    public void addFilmNameNull() {
        Film film = new Film();
        film.setName(null);
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");

        Throwable throwable = assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("create.film.name: Наименование фильма не может быть пустым или содержать только пробельные символы",
                throwable.getMessage(), "Текст ошибки валидации разный");
    }

    //Наименование фильма состоит только из пробельных символов
    @Test
    public void addFilmNameSpace() {
        Film film = new Film();
        film.setName(" ");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");

        Throwable throwable = assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("create.film.name: Наименование фильма не может быть пустым или содержать только пробельные символы",
                throwable.getMessage(), "Текст ошибки валидации разный");
    }

    //Длина описания фильма больше 200 символов
    @Test
    public void addFilmDescriptionMorePossibleCountSymbol() {
        String description = "а".repeat(201);

        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDescription(description);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();

        assertEquals("Максимальная длина описания — 200 символов", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Описание фильма не заполнено
    @Test
    public void addFilmDescriptionEmpty() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now().minusYears(5));

        String validatorMessage = validator.validate(film).iterator().next().getMessage();

        assertEquals("Описание фильма не может быть пустым или содержать только пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //В описание фильма передан null
    @Test
    public void addFilmDescriptionNull() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDescription(null);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();

        assertEquals("Описание фильма не может быть пустым или содержать только пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //Дата релиза раньше чем 28 декабря 1895 года
    @Test
    public void addFilmReleaseDateEarlyPossibleDate() {
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
    public void addFilmDurationLessPossible() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-30);

        Throwable throwable = assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("create.film.duration: Продолжительность фильма должна быть положительной",
                throwable.getMessage(), "Текст ошибки валидации разный");
    }

    //Обновление данных фильма
    @Test
    public void updateFilmData() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Наименование");
        film1.setDescription("Описание");
        film1.setDuration(45);
        film1.setReleaseDate(LocalDate.of(1991, 12, 4));
        film1.setMpa(new Mpa(2, "Тест2"));
        filmController.update(film1);

        assertNotEquals(film, film1, "Данные фильма совпадают");
    }

    //Обновление данных неизвестного фильма
    @Test
    public void updateUnknownFilmData() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(2);
        film1.setName("Наименование");
        film1.setDescription("Описание");
        film1.setDuration(45);
        film1.setReleaseDate(LocalDate.of(1991, 12, 4));
        film1.setMpa(new Mpa(2, "Тест2"));

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            filmController.update(film1);
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление данных фильма (передача null наименование фильма)
    @Test
    public void updateFilmWithNullName() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(1);
        film1.setName(null);
        film1.setDescription("Описание фильма");
        film1.setDuration(30);
        film1.setReleaseDate(LocalDate.of(1990, 11, 9));
        film1.setMpa(new Mpa(2, "Тест2"));

        Throwable throwable = assertThrows(ConstraintViolationException.class, () -> {
            filmController.update(film1);
        });

        assertEquals("update.film.name: Наименование фильма не может быть пустым или содержать только пробельные символы",
                throwable.getMessage(), "Текст ошибки валидации разный");
    }

    //Обновление данных фильма (передача null в описание фильма)
    @Test
    public void updateFilmWithNullDescription() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Имя");
        film1.setDescription(null);
        film1.setDuration(30);
        film1.setReleaseDate(LocalDate.of(1990, 11, 9));
        film1.setMpa(new Mpa(2, "Тест2"));

        Throwable throwable = assertThrows(ConstraintViolationException.class, () -> {
            filmController.update(film1);
        });

        assertEquals("update.film.description: Описание фильма не может быть пустым или содержать только пробельные символы",
                throwable.getMessage(), "Текст ошибки валидации разный");
    }

    //Получение всех фльмов
    @Test
    public void getAllFilms() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setName("Наименование фильма1");
        film1.setDescription("Описание фильма1");
        film1.setDuration(50);
        film1.setReleaseDate(LocalDate.of(1995, 2, 12));
        film1.setMpa(new Mpa(2, "Тест2"));
        filmController.create(film1);

        List<Film> filmList = filmController.getAll();

        assertEquals(filmList.size(), 2, "Количество фильмов не совпадают");
    }

    //Получение фильма по идентификатору
    @Test
    public void getFilmId() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(genres);
        filmController.create(film);

        Film film1 = new Film();
        film1.setName("Наименование фильма1");
        film1.setDescription("Описание фильма1");
        film1.setDuration(50);
        film1.setReleaseDate(LocalDate.of(1995, 2, 12));
        film1.setMpa(new Mpa(2, "Тест"));
        filmController.create(film1);

        List<Film> filmList = filmController.getAll();

        assertEquals(filmList.size(), 2, "Количество фильмов не совпадают");
        assertEquals(filmList.get(0).getId(), film.getId(), "Фильмы не совпадают");
    }

    //Удаление фильма по идентификатору
    @Test
    public void removeFilmId(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(genres);
        filmController.create(film);

        filmController.removeFilm(film.getId());

        List<Film> filmList = filmController.getAll();

        assertEquals(filmList.size(), 0, "Фильм не удален");
    }

    //Удаление несуществующего фильма
    @Test
    public void removeUnknownFilmId(){
        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            filmController.removeFilm(1);
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение неизвестного фильма по идентификатору
    @Test
    public void getUnknownFilmId() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            filmController.getFilm(2);
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Поставить лайк фильму
    @Test
    public void addLikeFilm() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());

        List<Film> likesFilm = filmController.getPopularFilm(10, null, null);

        assertEquals(likesFilm.size(), 1, "Лайк фильму не поставлен");
    }

    //Поставить лайк неизвестному фильму
    @Test
    public void addLikeUnknownFilm() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            filmController.addLikeFilm(1, user.getId());
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Удаление лайка с фильма
    @Test
    public void removeLikeFilm() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());
        filmController.removeLikeFilm(film.getId(), user.getId());

        List<Integer> likesFilm = filmDbStorage.getLike(film.getId());

        assertEquals(likesFilm.size(), 0, "Лайк у фильма остался");
    }

    //Удаление лайка с неизвестного фильма
    @Test
    public void removeLikeUnknownFilm() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            filmController.removeLikeFilm(1, user.getId());
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение самых популярных фильмов по кол-ву лайков или получение первых 10 фильмов
    @Test
    public void getPopularFilm() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setName("Наименование фильма1");
        film1.setDescription("Описание фильма1");
        film1.setDuration(50);
        film1.setReleaseDate(LocalDate.of(1995, 2, 12));
        film1.setMpa(new Mpa(2, "Тест2"));
        filmController.create(film1);

        Film film2 = new Film();
        film2.setName("Наименование фильма без лайка");
        film2.setDescription("Описание фильма без лайка");
        film2.setDuration(30);
        film2.setReleaseDate(LocalDate.of(2000, 2, 2));
        film2.setMpa(new Mpa(3, "Тест3"));
        filmController.create(film2);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());
        filmController.addLikeFilm(film1.getId(), user.getId());

        List<Film> popularFilm = filmController.getPopularFilm(2, null, null);

        assertEquals(popularFilm.size(), 2, "Количество не совпадает");
    }

    //Получение самых популярных фильмов по кол-ву лайков с фильтрацией по году
    @Test
    public void getPopularFilmWithReleaseYearShouldReturn2Films() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setName("Наименование фильма1");
        film1.setDescription("Описание фильма1");
        film1.setDuration(50);
        film1.setReleaseDate(LocalDate.of(1992, 2, 12));
        film1.setMpa(new Mpa(2, "Тест2"));
        filmController.create(film1);

        Film film2 = new Film();
        film2.setName("Наименование фильма2");
        film2.setDescription("Описание фильма2");
        film2.setDuration(30);
        film2.setReleaseDate(LocalDate.of(2000, 2, 2));
        film2.setMpa(new Mpa(3, "Тест3"));
        film2.setGenres(genres);
        filmController.create(film2);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());
        filmController.addLikeFilm(film1.getId(), user.getId());
        filmController.addLikeFilm(film2.getId(), user.getId());

        List<Film> popularFilm = filmController.getPopularFilm(10, null, 1992);

        assertEquals(popularFilm.size(), 2, "Количество не совпадает");
    }

    //Получение самых популярных фильмов по кол-ву лайков с фильтрацией по году
    @Test
    public void getPopularFilmWithReleaseYearShouldReturn0Films() {
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        film.setMpa(new Mpa(1, "Тест"));
        filmController.create(film);

        Film film1 = new Film();
        film1.setName("Наименование фильма1");
        film1.setDescription("Описание фильма1");
        film1.setDuration(50);
        film1.setReleaseDate(LocalDate.of(1992, 2, 12));
        film1.setMpa(new Mpa(2, "Тест2"));
        filmController.create(film1);

        Film film2 = new Film();
        film2.setName("Наименование фильма2");
        film2.setDescription("Описание фильма2");
        film2.setDuration(30);
        film2.setReleaseDate(LocalDate.of(2000, 2, 2));
        film2.setMpa(new Mpa(3, "Тест3"));
        film2.setGenres(genres);
        filmController.create(film2);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());
        filmController.addLikeFilm(film1.getId(), user.getId());
        filmController.addLikeFilm(film2.getId(), user.getId());

        List<Film> popularFilm = filmController.getPopularFilm(10, null, 1995);

        assertEquals(popularFilm.size(), 0, "Количество не совпадает");
    }
}
