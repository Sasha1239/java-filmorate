package ru.yandex.practicum.filmorate.controllerTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserCotrollerTest extends FilmorateApplicationTests {
    private final UserController userController;

    //Почта пользователя не заполнена
    @Test
    public void addUserEmailEmpty() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Почта не может быть пустой или содержать пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //В почту пользователя передан null
    @Test
    public void addUserEmailNull() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        user.setEmail(null);
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Почта не может быть пустой или содержать пробельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //В почте пользователя содержаться только пробельные символы
    @Test
    public void addUserEmailSpace() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setName("Наименование пользователя");
        user.setBirthday(LocalDate.now().minusYears(5));
        user.setEmail(" ");
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Неправильно написали почту",
                validatorMessage, "Текст ошибки валидации разный");
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

        assertEquals("Неправильно написали почту",
                validatorMessage, "Текст ошибки валидации разный");
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

    //В имя пользователя передан null
    @Test
    public void addUserNameNull() {
        User user = new User();
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setName(null);
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
        assertEquals("Логин не может быть пустой или содержать побельные символы",
                validatorMessage, "Текст ошибки валидации разный");
    }

    //Логин пользователя содержит пробелы
    @Test
    public void addUserLoginSpace() {
        User user = new User();
        user.setLogin(" ");
        user.setName("Наименование пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1960, 10, 20));
        userController.create(user);

        String validatorMessage = validator.validate(user).iterator().next().getMessage();
        assertEquals("Логин не может быть пустой или содержать побельные символы", validatorMessage,
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
    public void updateUserData() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user1.setId(user.getId());
        user1.setName("Имя");
        user1.setLogin("Логин");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        userController.update(user1);

        assertNotEquals(user, user1, "Данные пользователя совпадают");
    }

    //Обновление данных неизвестного пользователя
    @Test
    public void updateUnknownUserData() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user1.setId(2);
        user1.setName("Наименование");
        user1.setLogin("Логин");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            userController.update(user1);
        });
        assertEquals("Попробуйте другой идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление данных пользователя (передача null в имя пользователя)
    @Test
    public void updateUserDataWithNullName() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 11, 11));
        userController.create(user);

        User user1 = new User();
        user1.setId(user.getId());
        user1.setName(null);
        user1.setLogin("Логин");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        userController.update(user1);

        User userUpdate = userController.getUser(user.getId());

        assertNotEquals(user, userUpdate, "Значение равны");
        assertEquals(userUpdate.getName(), userUpdate.getLogin(), "Значения неравны");
    }

    //Обновление данных пользователя (передача null в почту пользователя)
    @Test
    public void updateUserDataWithNullEmail() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 11, 11));
        userController.create(user);

        User user1 = new User();
        user1.setId(user.getId());
        user1.setName("Наименование");
        user1.setLogin("Логин");
        user1.setEmail(null);
        user1.setBirthday(LocalDate.of(1991, 11, 11));

        Throwable throwable = assertThrows(ValidationException.class, () -> {
            userController.update(user1);
        });

        assertEquals("Используйте не null значения", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление данных пользователя (передача null в логин пользователя)
    @Test
    public void updateUserDataWithNullLogin() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 11, 11));
        userController.create(user);

        User user1 = new User();
        user1.setId(user.getId());
        user1.setName("Наименование");
        user1.setLogin(null);
        user1.setEmail("test1@uande.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        Throwable throwable = assertThrows(ValidationException.class, () -> {
            userController.update(user1);
        });

        assertEquals("Используйте не null значения", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Добавление в друзья
    @Test
    public void addFriend() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user1.setName("Наименование пользователя1");
        user1.setLogin("Логин пользователя1");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        userController.create(user1);

        userController.addFriend(user.getId(), user1.getId());

        List<User> friends = userController.getUserFriend(user.getId());

        assertEquals(friends.size(), 1, "У пользователя нет друзей");
    }

    //Добавление в друзья неизвестного пользователя
    @Test
    public void addUnknownFriend() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            userController.addFriend(user.getId(), 2);
        });
        assertEquals("Попробуйте другой идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Удаление из друзей
    @Test
    public void removeFriend() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user1.setName("Наименование пользователя1");
        user1.setLogin("Логин пользователя1");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        userController.create(user1);

        userController.addFriend(user.getId(), user1.getId());
        userController.removeFriend(user.getId(), user1.getId());

        List<User> friends = userController.getUserFriend(user.getId());

        assertEquals(friends.size(), 0, "У пользователя есть друзья");
    }

    //Удаление из друзей неизвестного пользователя
    @Test
    public void removeUnknownFriend() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            userController.removeFriend(user.getId(), 2);
        });
        assertEquals("Попробуйте другой идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //TODO Вывод друзей пользователя
    @Test
    public void getUserFriend() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user1.setName("Наименование пользователя1");
        user1.setLogin("Логин пользователя1");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        userController.create(user1);

        userController.addFriend(user.getId(), user1.getId());

        List<User> userFriend = userController.getUserFriend(user.getId());

        assertEquals(userFriend.get(0), user1, "Пользователи не совпадают");
    }

    //TODO Вывод общих друзей с пользователем
    @Test
    public void getCommonFriends() {
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user1.setName("Наименование пользователя1");
        user1.setLogin("Логин пользователя1");
        user1.setEmail("test1@yandex.ru");
        user1.setBirthday(LocalDate.of(1991, 11, 11));
        userController.create(user1);

        User user2 = new User();
        user2.setName("Наименование пользователя2");
        user2.setLogin("Логин пользователя2");
        user2.setEmail("test2@yandex.ru");
        user2.setBirthday(LocalDate.of(1992, 9, 12));
        userController.create(user2);

        userController.addFriend(user.getId(), user2.getId());
        userController.addFriend(user1.getId(), user2.getId());

        List<User> userCommonFriends = userController.getCommonFriends(user.getId(), user1.getId());
        System.out.println(userCommonFriends);
        assertEquals(userCommonFriends.get(0), user2, "Пользователи не совпадают");
    }
}
