package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    //Обновление пользователя
    public User update(User user) {
        log.debug("Запрос на обновление пользователя: {}", user);
        validateFindUserId(user.getId());
        return userStorage.update(user);
    }

    //Получение всех пользователей
    public List<User> getAll(){
        return userStorage.getAll();
    }

    //Получение пользователя по идентификатору
    public User getUser(int idUser){
        validateFindUserId(idUser);
        return userStorage.getUser(idUser);
    }

    //Добавление в друзья
    public void addFriend(int idUser, int idFriend){
        validateFindUserId(idUser);
        validateFindUserId(idFriend);
        userStorage.getUser(idUser).addFriend(idFriend);
        userStorage.getUser(idFriend).addFriend(idUser);
    }

    //Удаление из друзей
    public void removeFriend(int idUser, int idFriend){
        validateFindUserId(idUser);
        validateFindUserId(idFriend);
        userStorage.getUser(idUser).removeUserFriend(idFriend);
        userStorage.getUser(idFriend).removeUserFriend(idUser);
    }

    //Вывод друзей пользователя
    public List<User> getUserFriend(int idUser){
        validateFindUserId(idUser);
        List<User> userFriendList = userStorage.getAll().stream().filter(user ->
                userStorage.getUser(idUser).getFriends().contains(user.getId())).collect(Collectors.toList());
        return userFriendList;
    }

    //Вывод общих друзей с пользователем
    public List<User> getCommonFriends(int idUser, int idOtherUser){
        log.info("Запрос на вывод общих друзей между этим {} пользователем и этим {} пользователем",
                idUser, idOtherUser);
        validateFindUserId(idUser);
        validateFindUserId(idOtherUser);
        List<Integer> usersCommonFriends = new ArrayList<>(userStorage.getUser(idUser).getFriends());
        usersCommonFriends.retainAll(userStorage.getUser(idOtherUser).getFriends());

        List<User> commonFriends = userStorage.getAll().stream().filter(user ->
                usersCommonFriends.contains(user.getId())).collect(Collectors.toList());
        return commonFriends;
    }

    //Валидация пользователя
    private void validateFindUserId(int idUser){
        boolean userNoExists = userStorage.getAll().stream().noneMatch(user -> user.getId() == idUser);

        if (userNoExists){
            throw new NoSuchElementException("Попробуйте другой идентификатор пользователя");
        }
    }
}
