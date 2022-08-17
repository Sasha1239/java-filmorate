package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class Director {

    private long id;
    @NotBlank (message = "Имя режисера не может быть пустым")
    private String name;
}
