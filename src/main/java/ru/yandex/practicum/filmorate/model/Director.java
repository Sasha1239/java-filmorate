package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Director {

    @NonNull
    private long id;
    @NonNull
    private String name;
}
