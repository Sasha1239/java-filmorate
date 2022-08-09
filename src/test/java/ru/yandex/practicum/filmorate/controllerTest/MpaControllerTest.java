package ru.yandex.practicum.filmorate.controllerTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MpaControllerTest extends FilmorateApplicationTests {
    private final MpaController mpaController;

    //Получение MPA по идентификатору
    @Test
    public void getMpaId() {
        Mpa mpa = mpaController.getMpa(1);

        assertEquals(mpa.getId(), 1, "Идентификатор не совпадает");
        assertEquals(mpa.getName(), "G", "Наименования MPA не совпадают");
    }

    //Получение всех MPA
    @Test
    public void getAllMpa(){
        List<Mpa> mpaList = mpaController.getAllMpa();

        assertEquals(mpaList.size(), 5, "Количество MPA не совпадает");
    }

    //Получение несуществующего MPA
    @Test
    public void getMpaInvalidId(){
        Throwable throwable = assertThrows(NotFoundException.class, () -> {
            mpaController.getMpa(10);
        });

        assertEquals("Попробуйте другой идентификатор MPA", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }
}
