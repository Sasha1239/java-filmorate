package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private int id;
    @Email(message = "Неправильно написали почту")
    @NotBlank(message = "Почта не может быть пустой или содержать пробельные символы")
    private String email;
    @NotBlank(message = "Логин не может быть пустой или содержать побельные символы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    //Добавление друга
    public void addFriend(int idFriend){
        friends.add(idFriend);
    }

    //Удаление друга
    public void removeUserFriend(int idFriend){
        friends.remove(idFriend);
    }
}