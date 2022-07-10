package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest extends FilmorateApplicationTests{
    private final FilmController filmController;
    private final UserController userController;

    @Autowired
    public FilmControllerTest(FilmController filmController, UserController userController){
        this.filmController = filmController;
        this.userController = userController;
    }

    //Наименование фильма не заполнено
    @Test
    public void addFilmNameEmpty(){
        Film film = new Film();
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");
        filmController.create(film);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();
        assertEquals("Наименование фильма не может быть пустым или содержать только пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //В наименование фильма передается null
    @Test
    public void addFilmNameNull(){
        Film film = new Film();
        film.setName(null);
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");
        filmController.create(film);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();
        assertEquals("Наименование фильма не может быть пустым или содержать только пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //Наименование фильма состоит только из пробельных символов
    @Test
    public void addFilmNameSpace(){
        Film film = new Film();
        film.setName(" ");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("Описание фильма");
        filmController.create(film);

        String validatorMessage = validator.validate(film).iterator().next().getMessage();
        assertEquals("Наименование фильма не может быть пустым или содержать только пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
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

        String validatorMessage = validator.validate(film).iterator().next().getMessage();

        assertEquals("Максимальная длина описания — 200 символов", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Описание фильма не заполнено
    @Test
    public void addFilmDescriptionEmpty(){;
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
    public void addFilmDescriptionNull(){;
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

        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Наименование");
        film1.setDescription("Описание");
        film1.setDuration(45);
        film1.setReleaseDate(LocalDate.of(1991, 12, 4));
        filmController.update(film1);

        assertNotEquals(film, film1, "Данные фильма совпадают");
    }

    //Обновление данных неизвестного фильма
    @Test
    public void updateUnknownFilmData(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(2);
        film1.setName("Наименование");
        film1.setDescription("Описание");
        film1.setDuration(45);
        film1.setReleaseDate(LocalDate.of(1991, 12, 4));

        Throwable throwable = assertThrows(NoSuchElementException.class, () -> {
            filmController.update(film1);
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление данных фильма (передача null наименование фильма)
    @Test
    public void updateFilmWithNullName(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(1);
        film1.setName(null);
        film1.setDescription("Описание фильма");
        film1.setDuration(30);
        film1.setReleaseDate(LocalDate.of(1990, 11, 9));

        Throwable throwable = assertThrows(RuntimeException.class, () -> {
            filmController.update(film1);
        });

        assertEquals("Используйте не null значения", throwable.getMessage(),
                "Текст ошибки валидации разный");

    }

    //Обновление данных фильма (передача null в описание фильма)
    @Test
    public void updateFilmWithNullDescription(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Имя");
        film1.setDescription(null);
        film1.setDuration(30);
        film1.setReleaseDate(LocalDate.of(1990, 11, 9));

        Throwable throwable = assertThrows(RuntimeException.class, () -> {
            filmController.update(film1);
        });

        assertEquals("Используйте не null значения", throwable.getMessage(),
                "Текст ошибки валидации разный");
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

    //Получение фильма по идентификатору
    @Test
    public void getFilmId(){
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
        assertEquals(filmList.get(0), film, "Фильмы не совпадают");
    }

    //Получение неизвестного фильма по идентификатору
    @Test
    public void getUnknownFilmId(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        Throwable throwable = assertThrows(NoSuchElementException.class, () -> {
            filmController.getFilm(2);
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Поставить лайк фильму
    @Test
    public void addLikeFilm(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());

        Set<Integer> likesFilm = film.getLikesFilm();
        int[] likes = likesFilm.stream().mapToInt(Integer::intValue).toArray();

        assertEquals(likes[0], 1, "Лайк фильму не поставлен");
    }

    //Поставить лайк неизвестному фильму
    @Test
    public void addLikeUnknownFilm(){
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        Throwable throwable = assertThrows(NoSuchElementException.class, () -> {
            filmController.addLikeFilm(1, user.getId());
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Удаление лайка с фильма
    @Test
    public void removeLikeFilm(){
        Film film = new Film();
        film.setName("Наименование фильма");
        film.setDescription("Описание фильма");
        film.setDuration(50);
        film.setReleaseDate(LocalDate.of(1992, 12, 10));
        filmController.create(film);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());
        filmController.removeLikeFilm(film.getId(), user.getId());

        Set<Integer> likesFilm = film.getLikesFilm();

        assertEquals(likesFilm.size(), 0, "Лайк у фильма остался");
    }

    //Удаление лайка с неизвестного фильма
    @Test
    public void removeLikeUnknownFilm(){
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        Throwable throwable = assertThrows(NoSuchElementException.class, () -> {
            filmController.removeLikeFilm(1, user.getId());
        });

        assertEquals("Попробуйте другой идентификатор фильма", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение самых популярных фильмов по кол-ву лайков или получение первых 10 фильмов
    @Test
    public void getPopularFilm(){
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

        Film film2 = new Film();
        film2.setName("Наименование фильма без лайка");
        film2.setDescription("Описание фильма без лайка");
        film2.setDuration(30);
        film2.setReleaseDate(LocalDate.of(2000, 2, 2));
        filmController.create(film2);

        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        filmController.addLikeFilm(film.getId(), user.getId());
        filmController.addLikeFilm(film1.getId(), user.getId());

        List<Film> popularFilm = filmController.getPopularFilm(2);

        assertEquals(popularFilm.size(), 2, "Количество не совпадает");
    }
}
