package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{
    private final Map<Integer, User> users = new HashMap<>();
    private int idUser;

    //Добавление пользователя
    public User create(User user) {
        log.debug("Запрос на добавление пользователя: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(++idUser);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    //Обновление пользователя
    public User update(User user) {
        log.debug("Запрос на обновление пользователя: {}", user);

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Попробуйте другой идентификатор пользователя");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    //Получение всех пользователей
    public List<User> getAll(){
        return new ArrayList<>(users.values());
    }

    //Получение пользователя по идентификатору
    public User getUser(int idUser){
        return users.get(idUser);
    }
}
