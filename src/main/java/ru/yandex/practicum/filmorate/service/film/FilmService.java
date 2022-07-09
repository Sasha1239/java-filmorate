package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MAX_EARLY_DATE_FILM = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage){
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    //Добавление фильма
    public Film create(Film film){
        validateFilm(film);
        return filmStorage.create(film);
    }

    //Обновление фильма
    public Film update(Film film){
        validateFindFilmId(film.getId());
        validateFilm(film);
        return filmStorage.update(film);
    }

    //Получение всех фильмов
    public List<Film> getAll(){
        return filmStorage.getAll();
    }

    //Получение фильма по идентификатору
    public Film getFilm(int idFilm){
        validateFindFilmId(idFilm);
        return filmStorage.getFilm(idFilm);
    }

    //Пользователь ставит лайк фильму
    public void addLikeFilm(int idFilm, int idUser){
        validateFindFilmId(idFilm);
        validateFindUserId(idUser);
        filmStorage.getFilm(idFilm).addLike(idUser);
    }

    //Пользователь удаляет лайк
    public void removeLikeFilm(int idUser, int idFilm){
        validateFindFilmId(idFilm);
        validateFindUserId(idUser);
        filmStorage.getFilm(idFilm).removeLike(idUser);
    }

    //Получение самых популярных фильмов по кол-ву лайков или получение первых 10 фильмов
    public List<Film> getPopularFilm(int count){
        List<Film> popularFilms = filmStorage.getAll().stream().sorted(((o1, o2) ->
                o2.getLikesFilm().size() - o1.getLikesFilm().size())).limit(count).collect(Collectors.toList());
        return popularFilms;
    }

    //Валидация для поиска фильма по идентификатору
    private void validateFindFilmId(int idFilm){
        boolean filmNoExists = filmStorage.getAll().stream().noneMatch(film -> film.getId() == idFilm);

        if (filmNoExists){
            throw new NoSuchElementException("Попробуйте другой идентификатор фильма");
        }
    }

    //Валидация пользователя
    private void validateFindUserId(int idUser){
        boolean userNoExists = userStorage.getAll().stream().noneMatch(user -> user.getId() == idUser);

        if (userNoExists){
            throw new NoSuchElementException("Попробуйте другой идентификатор пользователя");
        }
    }

    //Валидация
    private void validateFilm(Film film){
        try {
            if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            if (film.getReleaseDate().isBefore(MAX_EARLY_DATE_FILM)){
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        } catch (ValidationException e){
            log.error("ValidationException", e);
            throw e;
        }
    }

}
