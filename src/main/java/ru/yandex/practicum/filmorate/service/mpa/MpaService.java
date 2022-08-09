package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    //Получение всех рейтингов
    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    //Получение рейтинга по идентификатору
    public Mpa getMpa(int idMpa){
        return mpaStorage.getMpa(idMpa).orElseThrow(() ->
                new NotFoundException("Попробуйте другой идентификатор MPA"));
    }
}
