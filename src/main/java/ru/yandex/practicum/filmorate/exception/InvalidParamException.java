package ru.yandex.practicum.filmorate.exception;

public class InvalidParamException extends IllegalArgumentException{
    public InvalidParamException(String s) {
        super(s);
    }
}
