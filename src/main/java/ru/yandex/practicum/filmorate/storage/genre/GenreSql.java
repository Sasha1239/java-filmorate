package ru.yandex.practicum.filmorate.storage.genre;

public class GenreSql {
    protected static final String GET_GENRE = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    protected static final String GET_GENRE_TO_FILM = "SELECT * FROM GENRE " +
            "LEFT JOIN FILM_GENRE FG ON GENRE.GENRE_ID = FG.GENRE_ID WHERE FG.FILM_ID = ?";
    protected static final String GET_ALL_GENRES = "SELECT * FROM GENRE";
    //protected static final String ADD_GENRE_TO_FILM = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
    protected static final String ADD_GENRE_TO_FILM = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
    protected static final String REMOVE_GENRE_TO_FILM = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
}
