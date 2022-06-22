package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Film {
    private int id;
    @NotNull(message = "Наименование фильма не может быть пустым")
    private String name;
    @NotNull(message = "Описание ильма не может быть пустым")
    private String description;
    @NotNull(message = "Время релиза не может быть пустым")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private long duration;
}