package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //Добавление пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    //Обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    //Получение всех пользователей
    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }

    //Получение пользователя по идентификатору
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUser(id);
    }

    //Удаление пользователя по идентификатору
    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable int userId){
        userService.removeUser(userId);
    }

    //Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId){
        userService.addFriend(id, friendId);
    }

    //Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId){
        userService.removeFriend(id, friendId);
    }

    //Вывод друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getUserFriend(@PathVariable int id){
        return userService.getUserFriend(id);
    }

    //Вывод общих друзей с пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId){
        return userService.getCommonFriends(id, otherId);
    }

    //Вывод рекомендаций
    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return userService.getRecommendations(id);
    }

    // Вывод ленты пользователя
    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable int id){
        return userService.getFeed(id);
    }

}