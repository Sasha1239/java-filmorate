package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> getAll();

    Optional<User> getUser(int idUser);

    void removeUser(int idUser);

    void addFriend(int idUser, int idFriend);

    List<User> getFriends(int idUser);

    List<User> getCommonsFriend(int idUser, int idFriend);

    void removeFriend(int idUser, int idFriend);
}
