package ru.yandex.practicum.filmorate.service.director;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Validated
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director getDirectorById (long id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Попробуйте другой идентификатор режисера"));
    }

    public List<Director> getDirectorList() {
        return directorStorage.getDirectorList();
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector (Director director){
        getDirectorById(director.getId());
        directorStorage.getDirectorById(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(long id) {
        directorStorage.deleteDirector(id);
    }
}
