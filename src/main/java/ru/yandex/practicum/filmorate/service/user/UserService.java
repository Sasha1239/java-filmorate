package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    //Добавление пользователя
    public User create(User user) {
        log.debug("Запрос на добавление пользователя: {}", user);
        validateNameUser(user);
        return userStorage.create(user);
    }

    //Обновление пользователя
    public User update(User user) {
        log.debug("Запрос на обновление пользователя: {}", user);
        getUser(user.getId());
        validateNameUser(user);
        validateUpdateUser(user);
        return userStorage.update(user);
    }

    //Получение всех пользователей
    public List<User> getAll(){
        return userStorage.getAll();
    }

    //Получение пользователя по идентификатору
    public User getUser(int idUser){
        return userStorage.getUser(idUser).orElseThrow(() ->
                new NotFoundException("Попробуйте другой идентификатор пользователя"));
    }

    //Добавление в друзья
    public void addFriend(int idUser, int idFriend){
        getUser(idUser);
        getUser(idFriend);
        userStorage.addFriend(idUser, idFriend);
    }

    //Удаление из друзей
    public void removeFriend(int idUser, int idFriend){
        getUser(idUser);
        getUser(idFriend);
        userStorage.removeFriend(idUser, idFriend);
    }

    //Вывод друзей пользователя
    public List<User> getUserFriend(int idUser){
        return userStorage.getFriends(idUser);
    }

    //Вывод общих друзей с пользователем
    public List<User> getCommonFriends(int idUser, int idOtherUser){
        log.info("Запрос на вывод общих друзей между этим {} пользователем и этим {} пользователем",
                idUser, idOtherUser);
        return userStorage.getCommonsFriend(idUser, idOtherUser);
    }

    //Валидация имени пользователя
    private void validateNameUser(User user){
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    //Валидация при обновлении пользователя
    private void validateUpdateUser(User user){
        if ((user.getLogin() == null) || (user.getEmail() == null)) {
            throw new ValidationException("Используйте не null значения");
        }
    }
}
