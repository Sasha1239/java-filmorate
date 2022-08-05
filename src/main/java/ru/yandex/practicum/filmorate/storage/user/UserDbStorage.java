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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(UserSql.CREATE_USER,
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
        //String sql = "UPDATE USERS SET USER_ID = ?, USER_NAME, EMAIL, LOGIN, BIRTHDAY) " +
        //String sql = "update USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ?  " +
                //"WHERE USER_ID = ?;";
        jdbcTemplate.update(UserSql.UPDATE_USER, user.getName(), user.getEmail(), user.getLogin(),
                user.getBirthday(), user.getId());
        return user;
    }

    @Override
    //Получение всех пользователей
    public List<User> getAll() {
        return jdbcTemplate.query(UserSql.GET_ALL_USERS, this::makeUser);
    }

    @Override
    //Получение пользователя по идентификатору
    public Optional<User> getUser(int idUser) {
        return jdbcTemplate.query(UserSql.GET_USER, this::makeUser, idUser).stream().findAny();
    }

    @Override
    public void removeUser(int idUser) {
        jdbcTemplate.update(UserSql.REMOVE_USER, idUser);
    }

    @Override
    public void addFriend(int idUser, int idFriend) {
        jdbcTemplate.update(UserSql.ADD_FRIEND, idUser, idFriend);
    }

    @Override
    public List<User> getFriends(int idUser) {
        String sql = "SELECT U.* FROM FRIENDS F " +
                "JOIN USERS U on F.FRIEND_ID = U.USER_ID " +
                "WHERE F.USER_ID = ?;";
        //List<User> friends = jdbcTemplate.query(UserSql.GET_FRIENDS, this::makeUser, idUser);
        //return jdbcTemplate.query(UserSql.GET_FRIENDS, this::makeUser, idUser);
        return jdbcTemplate.query(sql, this::makeUser, idUser);
    }

    @Override
    public List<User> getCommonsFriend(int idUser, int idFriend) {
        String sql = "SELECT U.* FROM FRIENDS F " +
                "JOIN USERS U ON F.FRIEND_ID = U.USER_ID " +
                "WHERE F.USER_ID = ? " +
                "UNION " +
                "SELECT U.* FROM FRIENDS F " +
                "JOIN USERS U ON F.FRIEND_ID = U.USER_ID " +
                "WHERE F.USER_ID = ?;";
        //return jdbcTemplate.query(UserSql.GET_COMMON_FRIENDS, this::makeUser, idUser, idFriend);
        return jdbcTemplate.query(sql, this::makeUser, idUser, idFriend);
    }

    @Override
    public void removeFriend(int idUser, int idFriend) {
        jdbcTemplate.update(UserSql.REMOVE_FRIEND, idUser, idFriend);
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
