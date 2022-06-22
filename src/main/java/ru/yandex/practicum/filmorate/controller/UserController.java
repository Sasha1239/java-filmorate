package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int idUser;

    //Добавление пользователя
    @PostMapping
    public void createUser(@Valid @RequestBody User user) {
        log.debug("Запрос на добавление пользователя: {}", user);

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(idUser++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
    }

    //Обновление пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Запрос на обновление пользователя: {}", user);

        if (user.getId() < 0) {
            throw new ValidationException("Попробуйте другой идентификатор пользователя");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }
}