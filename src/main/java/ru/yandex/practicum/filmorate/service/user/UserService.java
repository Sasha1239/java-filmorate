package ru.yandex.practicum.filmorate.service.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

    UserStorage userStorage;

    FilmStorage filmStorage;

    FeedStorage feedStorage;

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
    public List<User> getAll() {
        return userStorage.getAll();
    }

    //Получение пользователя по идентификатору
    public User getUser(int idUser) {
        return userStorage.getUser(idUser).orElseThrow(() ->
                new NotFoundException("Попробуйте другой идентификатор пользователя"));
    }

    //Удаление пользователя по идентификатору
    public void removeUser(int idUser) {
        getUser(idUser);
        userStorage.removeUser(idUser);
    }

    //Добавление в друзья
    public void addFriend(int idUser, int idFriend) {
        getUser(idUser);
        getUser(idFriend);
        userStorage.addFriend(idUser, idFriend);
        feedStorage.feed(idUser, idFriend, Event.FRIEND, Operation.ADD);
    }

    //Удаление из друзей
    public void removeFriend(int idUser, int idFriend) {
        getUser(idUser);
        getUser(idFriend);
        userStorage.removeFriend(idUser, idFriend);
        feedStorage.feed(idUser, idFriend, Event.FRIEND, Operation.REMOVE);
    }

    //Вывод друзей пользователя
    public List<User> getUserFriend(int idUser) {
        getUser(idUser);
        return userStorage.getFriends(idUser);
    }

    //Вывод общих друзей с пользователем
    public List<User> getCommonFriends(int idUser, int idOtherUser) {
        log.info("Запрос на вывод общих друзей между этим {} пользователем и этим {} пользователем",
                idUser, idOtherUser);
        return userStorage.getCommonsFriend(idUser, idOtherUser);
    }

    //Валидация имени пользователя
    private void validateNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    //Валидация при обновлении пользователя
    private void validateUpdateUser(User user) {
        if ((user.getLogin() == null) || (user.getEmail() == null)) {
            throw new ValidationException("Используйте не null значения");
        }
    }

    public List<Film> getRecommendations(int idUser) {
        // проверить наличие пользователя
        getUser(idUser);
        // собрать фильмы, которые понравились пользователям
        HashMap<Integer, List<Integer>> userLikedFilms = new HashMap<>();
        List<User> users = getAll();
        for (User user : users) {
            userLikedFilms.put(user.getId(), filmStorage.getUserLikedFilms(user.getId()));
        }
        // найти пользователей, с которыми максимальное пересечение
        long maxIntersection = 0;
        List<Integer> nearestUserIds = new ArrayList<>();
        List<Integer> filmListIds = userLikedFilms.get(idUser);
        for (Map.Entry<Integer, List<Integer>> entry : userLikedFilms.entrySet()) {
            if (entry.getKey() == idUser) {
                continue;
            }
            long intersection = entry.getValue().stream()
                    .filter(filmListIds::contains)
                    .count();
            if (maxIntersection == intersection) {
                nearestUserIds.add(entry.getKey());
            } else if (maxIntersection < intersection) {
                maxIntersection = intersection;
                nearestUserIds.clear();
                nearestUserIds.add(entry.getKey());
            }
        }
        // вернуть рекомендованные фильмы
        return nearestUserIds.stream()
                .map(userLikedFilms::get)
                .flatMap(Collection::stream)
                .distinct()
                .filter(filmId -> !filmListIds.contains(filmId))
                .sorted()
                .map(filmId -> filmStorage.getFilm(filmId).orElseThrow(
                                () -> new NotFoundException("Попробуйте другой идентификатор фильма")))
                .collect(Collectors.toList());
    }

    // Вывод ленты пользователя
    public List<Feed> getFeed(int userId) {
        getUser(userId);
        return feedStorage.getByUserId(userId);
    }
}
