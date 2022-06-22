package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
    private static Validator validator;
    private final UserController userController = new UserController();
    private final FilmController filmController = new FilmController();

    @BeforeAll
    public static void BeforeAll() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    public void contextLoads() {
    }

    //Почта пользователя не заполнена
    @Test
    public void addUserEmailEmpty() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Почта не может быть пустой", validatorMessage, "Текст ошибки валидации разный");
    }

    //Почта пользователя без @
    @Test
    public void addUserEmailWithoutDog() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setEmail("testYandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Неправильно написали почту", validatorMessage, "Текст ошибки валидации разный");
    }

    //Имя пользователя не заполнено
    @Test
    public void addUserNameEmpty() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1900, 10, 10));

        userController.create(user);
        assertEquals(user.getLogin(), user.getName(), "Значения неравны");
    }

    //Логин пользователя не заполнен
    @Test
    public void addUserEmptyLogin() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1965, 1, 19));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Логин не может быть пустой", validatorMessage, "Текст ошибки валидации разный");
    }

    //Логин пользователя содержит пробелы
    @Test
    public void addUserLoginSpace() {
		User user = new User();
        user.setLogin(" ");
        user.setName("Наименование пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1960, 10 , 20));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Логин не может содержать только пробельный символы", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Дата рождения пользователя в будущем
    @Test
    public void addUserBirthdayInFuture() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.now().plusYears(5));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Дата рождения не может быть в будущем", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Обновление данных пользователя
    @Test
    public void updateUserData(){
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = userController.update(new User(0, "test1@yandex.ru", "Логин", "Имя",
                LocalDate.of(1991, 12, 11)));

        assertNotEquals(user, user1, "Данные пользователя совпадают");
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

        Film film1 = filmController.update(new Film(0, "Наименование", "Описание",
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
