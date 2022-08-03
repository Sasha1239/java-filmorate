package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;

@SpringBootTest
public
class FilmorateApplicationTests {
    protected static Validator validator;

    @BeforeAll
    public static void BeforeAll() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    public void contextLoads() {
    }
}
