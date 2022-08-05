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
        return jdbcTemplate.query(MpaSql.GET_MPA, this::makeMpa, idMpa).stream().findAny();
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(MpaSql.GET_ALL_MPA, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNow) throws SQLException {
        int idMpa = resultSet.getInt("MPA_RATING_ID");
        String mpaName = resultSet.getString("mpa_name");
        return new Mpa(idMpa, mpaName);
    }
}
