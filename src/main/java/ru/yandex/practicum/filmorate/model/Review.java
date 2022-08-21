package ru.yandex.practicum.filmorate.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Review {

    int reviewId;
    @NonNull
    String content;
    @NonNull
    Boolean isPositive;
    @NonNull
    Integer userId;
    @NonNull
    Integer filmId;
    int useful;
}
