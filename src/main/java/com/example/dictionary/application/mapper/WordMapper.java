package com.example.dictionary.application.mapper;

import com.example.dictionary.application.dto.CommentDto;
import com.example.dictionary.application.dto.DefinitionDto;
import com.example.dictionary.application.dto.DictionaryDto;
import com.example.dictionary.application.dto.UserDto;
import com.example.dictionary.application.dto.WordDto;
import com.example.dictionary.domain.entity.Comment;
import com.example.dictionary.domain.entity.Definition;
import com.example.dictionary.domain.entity.Dictionary;
import com.example.dictionary.domain.entity.User;
import com.example.dictionary.domain.entity.Word;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface WordMapper {

    @Mapping(target = "synonyms", ignore = true)
    @Mapping(target = "antonyms", ignore = true)
    Word wordDtoToWord(WordDto wordDto);

    @Mapping(target = "synonyms", source = "synonyms", qualifiedByName = "wordsToWordsName")
    @Mapping(target = "antonyms", source = "antonyms", qualifiedByName = "wordsToWordsName")
    @Mapping(target = "contributors", source = "contributors", qualifiedByName = "userToUserDtoNames")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "commentsToCommentsDetails")
    @Mapping(target = "dictionaries", source = "dictionaries", qualifiedByName = "dictionariesToDictionariesDtoDetails")
    @Mapping(target = "definitions", source = "definitions", qualifiedByName = "definitionsToDefinitionsDtoDetails")
    WordDto wordToWordDto(Word word);

    @Named("userToUserDtoNames")
    static Set<UserDto> userToUserDtoNames(Set<User> users) {
        Set<UserDto> userDtos = new HashSet<>();
        users
                .forEach(user -> {
                    UserDto userDto = new UserDto();
                    userDto.setFirstName(user.getFirstName());
                    userDto.setLastName(user.getLastName());
                    userDto.setEmail(user.getEmail());
                    userDto.setRegisteredAt(user.getRegisteredAt());
                    userDtos.add(userDto);
                });

        return userDtos;
    }

    @Named("wordsToWordsName")
    static Set<WordDto> wordsToWordsName(Set<Word> words) {
        Set<WordDto> wordDtos = new HashSet<>();
        words.forEach(
                word -> {
                    WordDto wordDto = new WordDto();
                    wordDto.setName(word.getName());
                    wordDtos.add(wordDto);
                }
        );
        return wordDtos;
    }

    @Named("commentsToCommentsDetails")
    static List<CommentDto> commentsToCommentsDetails(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(
                comment -> {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setId(comment.getId());
                    commentDto.setCommentedAt(comment.getCommentedAt());
                    commentDto.setText(comment.getText());
                    commentDtos.add(commentDto);
                }
        );
        return commentDtos;
    }

    @Named("dictionariesToDictionariesDtoDetails")
    static Set<DictionaryDto> dictionariesToDictionariesDtoDetails(Set<Dictionary> dictionaries) {
        Set<DictionaryDto> dictionaryDtos = new HashSet<>();
        dictionaries.forEach(
                dictionary -> {
                    DictionaryDto dictionaryDto = new DictionaryDto();
                    dictionaryDto.setName(dictionary.getName());
                    dictionaryDto.setUrl(dictionary.getUrl());
                    dictionaryDtos.add(dictionaryDto);
                }
        );

        return dictionaryDtos;
    }

    @Named("definitionsToDefinitionsDtoDetails")
    static Set<DefinitionDto> definitionsToDefinitionsDtoDetails(Set<Definition> definitions) {
        Set<DefinitionDto> definitionDtos = new HashSet<>();
        definitions.forEach(
                definition -> {
                    DefinitionDto definitionDto = new DefinitionDto();
                    definitionDto.setText(definition.getText());
                    definitionDtos.add(definitionDto);
                }
        );

        return definitionDtos;
    }
}
