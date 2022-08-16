package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Film {
    private int id;
    @NotBlank(message = "Наименование фильма не может быть пустым или содержать только пробельные символы")
    private String name;
    @NotBlank(message = "Описание фильма не может быть пустым или содержать только пробельные символы")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Время релиза не может быть пустым")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private long duration;
    private Mpa mpa;
    private List<Genre> genres;
    private Set<Director> directors;
}