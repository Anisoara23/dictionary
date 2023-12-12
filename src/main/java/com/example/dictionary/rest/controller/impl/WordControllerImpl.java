package com.example.dictionary.rest.controller.impl;

import com.example.dictionary.application.dto.WordDto;
import com.example.dictionary.application.facade.WordFacade;
import com.example.dictionary.rest.controller.WordController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("")
public class WordControllerImpl implements WordController {

    private final WordFacade wordFacade;

    public WordControllerImpl(WordFacade wordFacade) {
        this.wordFacade = wordFacade;
    }

    @Override
    @GetMapping("words")
    public ResponseEntity<List<WordDto>> getAllWords() {
        List<WordDto> words = wordFacade.getAllWords();
        return new ResponseEntity<>(words, OK);
    }

    @GetMapping("words/{name}")
    public ResponseEntity<WordDto> getWordByName(@PathVariable("name") String name) {
        WordDto word = wordFacade.getWordByName(name);
        return new ResponseEntity<>(word, OK);
    }
}