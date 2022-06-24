package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idUser;

    //Добавление пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
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
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Запрос на обновление пользователя: {}", user);

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Попробуйте другой идентификатор пользователя");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    //Получение всех пользователей
    @GetMapping
    public List<User> getAll(){
        return new ArrayList<>(users.values());
    }
}