package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class Director {

    private long id;
    @NotBlank (message = "Имя режисера не может быть пустым")
    private String name;
}
