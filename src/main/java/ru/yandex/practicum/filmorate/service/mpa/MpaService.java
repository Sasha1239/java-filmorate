package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Optional<Mpa> getMpa(int idMpa){
        return Optional.ofNullable(mpaStorage.getMpa(idMpa).orElseThrow(() ->
                new NotFoundException("Попробуйте другой идентификатор MPA")));
    }
}
