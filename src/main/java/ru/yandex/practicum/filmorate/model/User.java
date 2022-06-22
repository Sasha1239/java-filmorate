package ru.yandex.practicum.filmorate.model;

import lombok.*;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

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
    @NotNull(message = "Логин не может быть пустой")
    @NotBlank(message = "Логин не может содержать только пробельный символы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}