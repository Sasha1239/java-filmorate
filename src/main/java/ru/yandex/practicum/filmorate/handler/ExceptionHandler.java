package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.InvalidParamException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e){
        log.warn("404 {}", e.getMessage(), e);
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException (NotFoundException e){
        log.warn("500 {}", e.getMessage(), e);
        return Map.of(
                "error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage()
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleNoSuchServerException (Throwable throwable){
        log.warn("500 {}", throwable.getMessage(), throwable);
        return Map.of(
                "error", "Ошибка сервера",
                "errorMessage", throwable.getMessage()
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus (HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidParamException (InvalidParamException e){
        log.warn("400 {}", e.getMessage());
        return Map.of("error", "Ошибка в параметрах", "errorMessage", e.getMessage());
    }
}
