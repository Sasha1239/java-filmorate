package ru.yandex.practicum.filmorate.controllerTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreControllerTest extends FilmorateApplicationTests {
    private final GenreController genreController;

    //Получение жанра по идентификатору
    @Test
    public void getGenreId(){
        Genre genre = genreController.getGenre(1);

        assertEquals(genre.getId(), 1, "Идентификатор не совпадает");
        assertEquals(genre.getName(), "Комедия", "Наименования жанров не совпадают");
    }

    //Получение всех жанров
    @Test
    public void getAllGenres(){
        List<Genre> genres = genreController.getAllGenres();

        assertEquals(genres.size(), 6, "Количество жанров не совпадает");
    }

    //Получение несуществующего жанра
    @Test
    public void getGenreInvalidId(){
        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            genreController.getGenre(10);
        });

        assertEquals("Попробуйте другой идентификатор жанра", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }
}
