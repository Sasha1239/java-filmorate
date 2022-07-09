package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
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
    @NotNull(message = "Почта не может быть пустой")
    @Email(message = "Неправильно написали почту")
    private String email;
    @NotNull(message = "Логин не может быть пустой или содержать побельные символы")
    @NotBlank(message = "Логин не может быть пустой или содержать побельные символы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
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