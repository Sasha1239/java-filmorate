package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ReviewLike {

    int userId;
    int reviewId;
    int isUseful;
}
