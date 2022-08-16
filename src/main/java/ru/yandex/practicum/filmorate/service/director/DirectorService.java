package ru.yandex.practicum.filmorate.service.director;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.Valid;
import java.util.List;

@Service
@AllArgsConstructor
@Validated
public class DirectorService {

    DirectorStorage directorStorage;

    public Director getDirectorById (int id) {
        return directorStorage.getDirectorById(id);
    }

    public List<Director> getDirectorList() {
        return directorStorage.getDirectorList();
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector (Director director){
        directorStorage.getDirectorById(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(long id) {
        directorStorage.deleteDirector(id);
    }
}
