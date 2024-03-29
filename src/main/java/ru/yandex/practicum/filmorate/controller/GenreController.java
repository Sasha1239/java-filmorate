package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService){
        this.genreService = genreService;
    }

    //Получение всех жанров
    @GetMapping
    public List<Genre> getAllGenres(){
        return genreService.getAllGenres();
    }

    //Получение жанра по идентификатору
    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable("id") int idGenre){
        return genreService.getGenre(idGenre);
    }
}
