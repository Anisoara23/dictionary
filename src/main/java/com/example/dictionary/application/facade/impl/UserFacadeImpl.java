package com.example.dictionary.application.facade.impl;

import com.example.dictionary.application.dto.AchievementDto;
import com.example.dictionary.application.dto.UserDto;
import com.example.dictionary.application.dto.WordDto;
import com.example.dictionary.application.exception.ResourceNotFoundException;
import com.example.dictionary.application.facade.UserFacade;
import com.example.dictionary.application.facade.WordFacade;
import com.example.dictionary.application.mapper.AchievementMapper;
import com.example.dictionary.application.mapper.UserMapper;
import com.example.dictionary.application.mapper.WordMapper;
import com.example.dictionary.application.security.key.KeyRoleExtractor;
import com.example.dictionary.application.security.util.SecurityUtils;
import com.example.dictionary.application.util.ImageUtils;
import com.example.dictionary.application.validator.UserValidator;
import com.example.dictionary.domain.entity.User;
import com.example.dictionary.domain.entity.UserInfo;
import com.example.dictionary.domain.entity.Word;
import com.example.dictionary.domain.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;

    private final UserMapper userMapper;

    private final UserValidator userValidator;

    private final PasswordEncoder passwordEncoder;

    private final AchievementMapper achievementMapper;

    private final WordFacade wordFacade;

    private final WordMapper wordMapper;

    private final MessageSource messageSource;

    public UserFacadeImpl(UserService userService,
                          UserMapper userMapper,
                          UserValidator userValidator,
                          PasswordEncoder passwordEncoder,
                          AchievementMapper achievementMapper,
                          WordFacade wordFacade,
                          WordMapper wordMapper,
                          MessageSource messageSource) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.achievementMapper = achievementMapper;
        this.wordFacade = wordFacade;
        this.wordMapper = wordMapper;
        this.messageSource = messageSource;
    }

    @Override
    public UserDto registerUser(UserDto userDto) {
        userValidator.validate(userDto);

        User user = userMapper.userDtoToUser(userDto);

        user.setRole(KeyRoleExtractor.getRole(userDto.getKey()));
        user.setRegisteredAt(LocalDate.now());

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setUserInfo(new UserInfo(0));
        User registeredUser = userService.registerUser(user);
        return userMapper.userToUserDto(registeredUser);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = getUser(email);

        return userMapper.userToUserDto(user);
    }

    @Override
    public Integer updateUserProgress(UserDto user) {
        String email = user.getEmail();
        User userToUpdate = getUser(email);

        Integer progress = userToUpdate.getUserInfo().getProgress() + 1;

        userToUpdate.getUserInfo().setProgress(progress);
        User updatedUser = userService.registerUser(userToUpdate);
        return updatedUser.getUserInfo().getProgress();
    }

    @Override
    public UserDto uploadImage(MultipartFile file) throws IOException {
        User user = getUser(SecurityUtils.getUsername());
        user.setProfileImage(ImageUtils.compressImage(file.getBytes()));

        User updatedUser = userService.registerUser(user);
        byte[] addedLogo = ImageUtils.decompressImage(updatedUser.getProfileImage());

        UserDto userDto = userMapper.userToUserDto(user);
        userDto.setProfileImage(addedLogo);
        return userDto;
    }

    @Override
    public UserDto getUserProfile() {
        User user = getUser(SecurityUtils.getUsername());

        byte[] logo = getLogo(user);

        UserDto userDto = userMapper.userToUserDto(user);
        userDto.setProfileImage(logo);

        return userDto;
    }

    @Override
    public boolean updateUser(UserDto userDto) {
        User user = getUser(SecurityUtils.getUsername());

        boolean isChanged = setUserCredentials(userDto, user);

        userService.registerUser(user);
        return isChanged;
    }

    @Override
    public void addAchievement(AchievementDto achievement) {
        User user = getUser(SecurityUtils.getUsername());

        user.getUserInfo()
                .getAchievements()
                .add(achievementMapper.achievementDtoToAchievement(achievement));

        userService.registerUser(user);
    }

    @Override
    public Set<String> addWordToFavorities(String wordName) {
        User user = getUser(SecurityUtils.getUsername());
        Set<Word> favorites = user.getUserInfo().getFavorites();

        WordDto wordByName = wordFacade.getWordByName(wordName);
        favorites.add(wordMapper.wordDtoToWord(wordByName));

        userService.registerUser(user);

        return favorites.stream()
                .map(Word::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> removeWordFromFavorites(String wordName) {
        User user = getUser(SecurityUtils.getUsername());
        Set<Word> favorites = user.getUserInfo().getFavorites();

        Word wordToRemove = getWordToRemove(wordName, favorites, messageSource);

        favorites.remove(wordToRemove);
        userService.registerUser(user);

        return favorites.stream()
                .map(Word::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = new ArrayList<>();
        users.forEach(user -> {
            byte[] logo = getLogo(user);

            UserDto userDto = userMapper.userToUserDto(user);
            userDto.setProfileImage(logo);
            userDtos.add(userDto);
        });
        return userDtos;
    }

    private static byte[] getLogo(User user) {
        byte[] logo = null;
        byte[] dbLogo = user.getProfileImage();

        if (dbLogo != null) {
            logo = ImageUtils.decompressImage(dbLogo);
        }
        return logo;
    }

    private static Word getWordToRemove(String wordName, Set<Word> favorites, MessageSource messageSource) {
        return favorites
                .stream()
                .filter(word -> word.getName().equalsIgnoreCase(wordName))
                .findAny()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                messageSource.getMessage("user.favorite.error.not.found",
                                        new Object[]{wordName},
                                        Locale.getDefault()))
                );
    }

    private static boolean setUserCredentials(UserDto userDto, User user) {
        boolean isChanged = false;

        String firstName = UserMapper.capitalizeFirstLetter(userDto.getFirstName());
        if (firstName != null && !firstName.equalsIgnoreCase(user.getFirstName())) {
            user.setFirstName(firstName);
            isChanged = true;
        }

        String lastName = UserMapper.capitalizeFirstLetter(userDto.getLastName());
        if (lastName != null && !lastName.equalsIgnoreCase(user.getLastName())) {
            user.setLastName(lastName);
            isChanged = true;
        }

        return isChanged;
    }

    private User getUser(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("user.email.error.not.found",
                                new Object[]{email},
                                Locale.getDefault())));
    }
}
