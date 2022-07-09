package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserCotrollerTest extends FilmorateApplicationTests{
    private final UserController userController;

    @Autowired
    public UserCotrollerTest(UserController userController){
        this.userController = userController;
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
        user.setBirthday(LocalDate.of(1960, 10 , 20));
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

    //Добавление в друзья
    @Test
    public void addFriend(){
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

        Set<Integer> friends = user.getFriends();
        int[] userFriends = friends.stream().mapToInt(Integer::intValue).toArray();

        assertEquals(userFriends[0], user1.getId(), "У пользователя нет друзей");
    }

    //Удаление из друзей
    @Test
    public void removeFriend(){
        User user = new User();
        user.setName("Наименование пользователя");
        user.setLogin("Логин пользователя");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1990, 12, 10));
        userController.create(user);

        User user1 = new User();
        user.setName("Наименование пользователя1");
        user.setLogin("Логин пользователя1");
        user.setEmail("test1@yandex.ru");
        user.setBirthday(LocalDate.of(1991, 11, 11));
        userController.create(user1);

        userController.removeFriend(user.getId(), user1.getId());

        assertEquals(user.getFriends().size(), 0, "У пользователя есть друзья");
    }

    //TODO Вывод друзей пользователя - выводятся правильно, но работает с ошибкой
    @Test
    public void getUserFriend(){
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

        List<User> userFriend = userController.getUserFriend(user1.getId());

        assertEquals(userFriend.get(0), user, "Пользователи не совпадают");
    }

    //Вывод общих друзей с пользователем
    @Test
    public void getCommonFriends(){
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
        assertEquals(userCommonFriends.get(0), user2, "Пользователи не совпадают");
    }
}
