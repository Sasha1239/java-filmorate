package ru.yandex.practicum.filmorate.model.feed;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feed {

    @NotNull
    Integer eventId;

    @NotNull @PositiveOrZero
    Long timestamp;

    @NotNull
    Integer userId;

    @NotNull
    Event eventType;

    @NotNull
    Operation operation;

    @NotNull
    Integer entityId;

}
