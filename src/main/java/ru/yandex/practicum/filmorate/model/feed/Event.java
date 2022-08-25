package ru.yandex.practicum.filmorate.model.feed;

public enum Event {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);

    private final int id;

    Event(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
