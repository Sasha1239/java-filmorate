package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface FeedStorage {

    void feed(Integer userId, Integer entityId, Event event, Operation operation);

    List<Feed> getByUserId(Integer userId);

}
