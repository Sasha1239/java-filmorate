package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate MAX_EARLY_DATE_FILM = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final DirectorService directorService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, DirectorService directorService, GenreStorage genreStorage){
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorService = directorService;
        this.genreStorage = genreStorage;
    }

    //Добавление фильма
    public Film create(Film film){
        validateFilm(film);
        return filmStorage.create(film);
    }

    //Обновление фильма
    public Optional<Film> update(Film film){
        getFilm(film.getId());
        validateFilm(film);
        return filmStorage.update(film);
    }

    //Получение всех фильмов
    public List<Film> getAll(){
        return filmStorage.getAll();
    }

    public Film getFilm(int idFilm){
       return filmStorage.getFilm(idFilm).orElseThrow(() ->
               new NotFoundException("Попробуйте другой идентификатор фильма"));
    }

    //Удаление фильма по идентификатору
    public void removeFilm(int idFilm){
        getFilm(idFilm);
        filmStorage.removeFilm(idFilm);
    }

    //Пользователь ставит лайк фильму
    public void addLikeFilm(int idFilm, int idUser){
        validateFindUserId(idUser);
        getFilm(idFilm);
        filmStorage.addLikeFilm(idFilm, idUser);
    }

    //Пользователь удаляет лайк
    public void removeLikeFilm(int idFilm, int idUser){
        getFilm(idFilm);
        validateFindUserId(idUser);
        filmStorage.removeLikeFilm(idFilm, idUser);
    }


    //Поиск по названию фильмов и по режиссёру. Возвращает список фильмов, отсортированных по популярности.
    public List<Film> getPopularFilm(int count, Integer genreId, Integer year){
        // проверим валидность присланного жанра
        if (genreId != null) {
            genreStorage.getGenre(genreId).orElseThrow(() ->
                    new NotFoundException("Попробуйте задать другой жанр для фильтрации популярных фильмов"));
        }
        // проверим валидность присланного года
        if (year != null && year < 0) {
            new NotFoundException ("Год не может быть отрицательным");
        }
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    //Поиск по названию фильма и/или по режиссёру. Возвращает список фильмов, отсортированных по популярности.
    public List<Film> searchFilmsByNameByDirector(String searchStr, String searchBy) {
        //Так как searchStr и searchBy могут быть Null, то данные на валидность не проверяются
        return filmStorage.searchFilmsByNameByDirector(searchStr, searchBy);

    }
    public List<Film> getAllFilmsOfDirector (int directorId, String sortBy) {
        List<Film> films = new ArrayList<>();
        try {
            directorService.getDirectorById(directorId);
            films = filmStorage.getAllFilmOfDirector(directorId, sortBy);
        } catch (ValidationException e){
            log.error("ValidationException", e);
            throw e;
        }
        return films;
    }

    //Валидация пользователя
    private void validateFindUserId(int idUser){
        userStorage.getUser(idUser).orElseThrow(() ->
                new NotFoundException("Попробуйте другой идентификатор пользователя"));
    }

    //Валидация
    private void validateFilm(Film film){
        try {
            if (film.getReleaseDate().isBefore(MAX_EARLY_DATE_FILM)){
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        } catch (ValidationException e){
            log.error("ValidationException", e);
            throw e;
        }
    }
}
