package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {
    private int id;
    @NotBlank(message = "Почта не может быть пустой или содержать пробельные символы")
    @Email(message = "Неправильно написали почту")
    private String email;
    @NotBlank(message = "Логин не может быть пустой или содержать побельные символы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}