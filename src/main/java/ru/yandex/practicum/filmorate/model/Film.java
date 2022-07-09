package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Film {
    private int id;
    @NotNull(message = "Наименование фильма не может быть пустым или содержать только пробельные символы")
    @NotBlank(message = "Наименование фильма не может быть пустым или содержать только пробельные символы")
    private String name;
    @NotNull(message = "Описание ильма не может быть пустым")
    private String description;
    @NotNull(message = "Время релиза не может быть пустым")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private long duration;

    //Для хранения лайков в фильмах
    private Set<Integer> likesFilm = new HashSet<>();

    public void addLike(int idUser) {
        likesFilm.add(idUser);
    }

    public void removeLike(int idUser) {
        likesFilm.remove(idUser);
    }
}