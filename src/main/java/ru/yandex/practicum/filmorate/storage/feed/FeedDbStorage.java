package ru.yandex.practicum.filmorate.storage.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FeedDbStorage implements FeedStorage {

    JdbcTemplate jdbcTemplate;

    @Override
    public void feed(Integer userId, Integer entityId, Event event, Operation operation) {
        jdbcTemplate.update("INSERT INTO feeds(user_id, entity_id, event_id, operation_id) VALUES(?, ?, ?, ?)",
                userId, entityId, event.getId(), operation.getId());
    }

    @Override
    public List<Feed> getByUserId(Integer userId) {
        return jdbcTemplate.query("SELECT * FROM feeds JOIN feed_events USING(event_id) " +
                        "JOIN feed_operations USING(operation_id) WHERE user_id = ? ORDER BY feed_id",
                (resultSet, rowNum) -> makeFeed(resultSet), userId);
    }

    private Feed makeFeed(ResultSet resultSet) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getInt("feed_id"))
                .timestamp(resultSet.getLong("created"))
                .userId(resultSet.getInt("user_id"))
                .entityId(resultSet.getInt("entity_id"))
                .eventType(Event.valueOf(resultSet.getString("event_name")))
                .operation(Operation.valueOf(resultSet.getString("operation_name")))
                .build();
    }

}
