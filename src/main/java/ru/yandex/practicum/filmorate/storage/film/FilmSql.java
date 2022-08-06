package ru.yandex.practicum.filmorate.storage.film;

public class FilmSql {
    //protected static final String GET_FILM_ID = "SELECT * FROM FILM WHERE FILM_ID = ?;";
    protected static final String GET_FILM_ID = "SELECT * FROM FILM AS F " +
            "LEFT JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
            "LEFT JOIN MPA M ON M.MPA_RATING_ID = F.MPA_RATING " +
            "LEFT JOIN GENRE G ON G.GENRE_ID = FG.GENRE_ID " +
            "LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID " +
            "WHERE F.FILM_ID = ?;";
    protected static final String GET_ALL_FILMS = "SELECT * FROM FILM AS F " +
            "LEFT JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
            "LEFT JOIN MPA M ON M.MPA_RATING_ID = F.MPA_RATING " +
            "LEFT JOIN GENRE G ON G.GENRE_ID = FG.GENRE_ID;";
    protected static final String CREATE_FILM = "INSERT INTO FILM (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, " +
            "MPA_RATING) VALUES (?, ?, ?, ?, ?);";
    protected static final String UPDATE_FILM = "MERGE INTO FILM (FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, " +
            "DURATION, MPA_RATING) VALUES (?, ?, ?, ?, ?, ?);";
    protected static final String REMOVE_FILM = "DELETE FROM FILM WHERE FILM_ID = ?;";
    protected static final String GET_POPULAR_FILMS = "SELECT F.FILM_ID FROM FILM_LIKES FL " +
            "RIGHT JOIN FILM F on F.FILM_ID = FL.FILM_ID " +
            "GROUP BY F.FILM_ID ORDER BY COUNT(FL.FILM_ID) DESC LIMIT ?;";
    protected static final String LIKE_FILM = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?);";
    protected static final String GET_LIKES = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?;";
    protected static final String REMOVE_LIKE = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?;";
}
