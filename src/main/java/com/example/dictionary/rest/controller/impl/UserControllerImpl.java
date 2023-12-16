package com.example.dictionary.rest.controller.impl;

import com.example.dictionary.application.dto.UserDto;
import com.example.dictionary.application.facade.UserFacade;
import com.example.dictionary.rest.controller.UserController;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class UserControllerImpl implements UserController {

    private final UserFacade userFacade;

    public UserControllerImpl(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    @PostMapping("${api.registration}")
    @ResponseStatus(CREATED)
    @PermitAll
    public void registerUser(@RequestBody @Valid UserDto userDto) {
        userFacade.registerUser(userDto);
    }
}