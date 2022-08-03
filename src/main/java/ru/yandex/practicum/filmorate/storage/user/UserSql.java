package ru.yandex.practicum.filmorate.storage.user;

public class UserSql {
    protected static final String GET_USER = "SELECT * FROM USERS WHERE USER_ID = ?;";
    protected static final String GET_ALL_USERS = "SELECT * FROM USERS;";
    protected static final String CREATE_USER = "INSERT INTO USERS (USER_NAME, EMAIL, LOGIN, BIRTHDAY) " +
            "VALUES (?, ?, ?, ?);";
    protected static final String UPDATE_USER = "MERGE INTO USERS (USER_ID, USER_NAME, EMAIL, LOGIN, BIRTHDAY) " +
            "VALUES (?, ?, ?, ?, ?);";
    protected static final String REMOVE_USER = "DELETE FROM USERS WHERE USER_ID = ?;";
    //protected static final String GET_FRIENDS = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?;";
    protected static final String GET_FRIENDS = "SELECT U.* FROM USERS U " +
            "JOIN FRIENDS F on U.USER_ID = F.USER_ID " +
            "WHERE U.USER_ID = ?;";
    protected static final String ADD_FRIEND = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?);";
    protected static final String REMOVE_FRIEND = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?;";
    protected static final String GET_COMMON_FRIENDS = "SELECT U.* FROM FRIENDS F " +
            "JOIN USERS U ON F.FRIEND_ID = U.USER_ID " +
            "WHERE f.user_id = ? " +
            "UNION " +
            "SELECT U.* " +
            "FROM FRIENDS F " +
            "JOIN USERS U ON F.FRIEND_ID = U.USER_ID " +
            "WHERE F.USER_ID = ?;";
}
