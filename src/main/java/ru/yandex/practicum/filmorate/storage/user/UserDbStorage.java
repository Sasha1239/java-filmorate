package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    //Добавление пользователя
    public User create(User user) {
        final String createUserSql = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(createUserSql,
                    new String[]{"USER_ID"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            LocalDate birthday = user.getBirthday();

            if (birthday == null) {
                preparedStatement.setNull(4, Types.DATE);
            } else {
                preparedStatement.setDate(4, java.sql.Date.valueOf(birthday));
            }
            return preparedStatement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    //Обновление пользователя
    public User update(User user) {
        final String updateUserSql = "UPDATE USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ?  " +
                "WHERE USER_ID = ?;";

        jdbcTemplate.update(updateUserSql, user.getName(), user.getEmail(), user.getLogin(),
                user.getBirthday(), user.getId());
        return user;
    }

    @Override
    //Получение всех пользователей
    public List<User> getAll() {
        final String getAllUsersSql = "SELECT * FROM USERS;";

        return jdbcTemplate.query(getAllUsersSql, this::makeUser);
    }

    @Override
    //Получение пользователя по идентификатору
    public Optional<User> getUser(int idUser) {
        final String getUserSql = "SELECT * FROM USERS WHERE USER_ID = ?;";

        return jdbcTemplate.query(getUserSql, this::makeUser, idUser).stream().findAny();
    }

    //Удаление пользователя
    @Override
    public void removeUser(int idUser) {
        final String removeUserSql = "DELETE FROM USERS WHERE USER_ID = ?;";

        jdbcTemplate.update(removeUserSql, idUser);
    }

    //Добавление друга
    @Override
    public void addFriend(int idUser, int idFriend) {
        final String addFriendSql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?);";

        jdbcTemplate.update(addFriendSql, idUser, idFriend);
    }

    //Получение друзей
    @Override
    public List<User> getFriends(int idUser) {
        String getFriendsSql = "SELECT U.* FROM FRIENDS F " +
                "JOIN USERS U on F.FRIEND_ID = U.USER_ID " +
                "WHERE F.USER_ID = ?;";
        return jdbcTemplate.query(getFriendsSql, this::makeUser, idUser);
    }

    //Получение общих друзей
    @Override
    public List<User> getCommonsFriend(int idUser, int idFriend) {
        final String getCommonFriendSql = "SELECT U.* FROM FRIENDS F " +
                "JOIN USERS U ON F.FRIEND_ID = U.USER_ID " +
                "WHERE U.USER_ID = ? " +
                "UNION " +
                "SELECT U.* FROM FRIENDS F " +
                "JOIN USERS U ON F.FRIEND_ID = U.USER_ID " +
                "WHERE F.USER_ID = ?;";
        return jdbcTemplate.query(getCommonFriendSql, this::makeUser, idUser, idFriend);
    }

    //Удаление из друзей
    @Override
    public void removeFriend(int idUser, int idFriend) {
        final String removeFriendSql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?;";

        jdbcTemplate.update(removeFriendSql, idUser, idFriend);
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        int idUser = resultSet.getInt("USER_ID");
        String emailUser = resultSet.getString("EMAIL");
        String loginUser = resultSet.getString("LOGIN");
        String userName = resultSet.getString("USER_NAME");
        LocalDate birthdayUser = LocalDate.parse(resultSet.getString("BIRTHDAY"));
        return new User(idUser, emailUser, loginUser, userName, birthdayUser);
    }
}
