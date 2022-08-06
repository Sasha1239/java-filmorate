package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getMpa(int idMpa) {
        final String getMpaSql = "SELECT * FROM MPA WHERE MPA_RATING_ID = ?;";

        return jdbcTemplate.query(getMpaSql, this::makeMpa, idMpa).stream().findAny();
    }

    @Override
    public List<Mpa> getAllMpa() {
        final String getAllMpa = "SELECT * FROM MPA;";

        return jdbcTemplate.query(getAllMpa, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNow) throws SQLException {
        int idMpa = resultSet.getInt("MPA_RATING_ID");
        String mpaName = resultSet.getString("MPA_NAME");
        return new Mpa(idMpa, mpaName);
    }
}
