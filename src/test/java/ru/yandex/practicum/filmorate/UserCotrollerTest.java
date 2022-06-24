package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserCotrollerTest extends FilmorateApplicationTests{
    private final UserController userController = new UserController();

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

        User user1 = userController.update(new User(1, "test1@yandex.ru", "Логин", "Имя",
                LocalDate.of(1991, 12, 11)));

        assertNotEquals(user, user1, "Данные пользователя совпадают");
    }
}
