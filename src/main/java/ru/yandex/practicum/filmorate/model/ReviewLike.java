package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ReviewLike {

    private int userId;
    private int reviewId;
    private boolean isUseful;
}
