package com.example.dictionary.ui.profile;

import com.example.dictionary.application.dto.AchievementDto;
import com.example.dictionary.application.dto.UserDto;
import com.example.dictionary.application.dto.UserInfoDto;
import com.example.dictionary.application.dto.WordDto;
import com.example.dictionary.application.facade.AchievementFacade;
import com.example.dictionary.application.facade.UserFacade;
import com.example.dictionary.ui.security.CurrentUserPermissionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.util.List;
import java.util.Set;

import static com.example.dictionary.ui.util.UiUtils.APP_COLOR;
import static com.example.dictionary.ui.util.UiUtils.getCloseButton;

public class UserProgressLayout extends VerticalLayout {

    private final CurrentUserPermissionService permissionService;

    private Integer progress;

    private Set<AchievementDto> userAchievements;

    private List<AchievementDto> allAchievements;

    private Set<WordDto> favorites;

    private final UserFacade userFacade;

    private final AchievementFacade achievementFacade;

    private Grid<WordDto> favoriteWordsGrid;

    private UserInfoDto userInfo;

    public UserProgressLayout(CurrentUserPermissionService permissionService,
                              UserFacade userFacade,
                              AchievementFacade achievementFacade) {
        this.permissionService = permissionService;
        this.userFacade = userFacade;
        this.achievementFacade = achievementFacade;

        UserDto profile = userFacade.getUserProfile();
        userInfo = profile.getUserInfo();

        if (permissionService.hasWordWritePermission()) {
            setupProgressLayout();
            setupAchievementsGrid();
        }

        setupFavoriteWords();

        setWidth("43%");
    }

    private void setupProgressLayout() {
        progress = userInfo.getProgress();

        Span progressLabel = new Span(progress.toString());
        progressLabel.getStyle().set("color", APP_COLOR)
                .set("font-weight", "bold");

        Span progressDescription = new Span(getTranslation("profile.contributors") + " ");
        HorizontalLayout progressLayout = new HorizontalLayout(progressDescription, progressLabel);
        add(progressLayout);
    }

    private void setupAchievementsGrid() {
        allAchievements = achievementFacade.getAllAchievements();
        userAchievements = userInfo.getAchievements();

        Span achievementsLabel = new Span(getTranslation("profile.achievements"));
        Grid<AchievementDto> achievementGrid = new Grid<>();
        achievementGrid.setItems(allAchievements);
        achievementGrid.
                addColumn(AchievementDto::getName)
                .setWidth("30%")
                .setTooltipGenerator(achievementDto ->
                        String.valueOf(achievementDto.getNumberOfWordsRequired()));
        achievementGrid.addComponentColumn(this::createProgressBar).setWidth("70%");

        achievementGrid.getStyle().set("border", "none");
        achievementGrid.setWidth("110%");
        achievementGrid.setAllRowsVisible(true);
        add(achievementsLabel, achievementGrid);
    }

    private void setupFavoriteWords() {
        favorites = userInfo.getFavorites();

        favoriteWordsGrid = new Grid<>(WordDto.class, false);
        Details favouriteWordsDetails = new Details(getTranslation("profile.favorites"));
        favouriteWordsDetails.addThemeVariants(DetailsVariant.REVERSE);
        favouriteWordsDetails.setWidth("110%");


        favoriteWordsGrid.getStyle().set("border", "none");
        favoriteWordsGrid.setAllRowsVisible(true);

        favoriteWordsGrid.setItems(favorites);
        favoriteWordsGrid.addColumn(WordDto::getName)
                .setTextAlign(ColumnTextAlign.START);
        favoriteWordsGrid.addComponentColumn(this::getDeleteFavoriteWordButton)
                .setTextAlign(ColumnTextAlign.END);

        favouriteWordsDetails.add(favoriteWordsGrid);
        add(favouriteWordsDetails);
    }

    private Component createProgressBar(AchievementDto achievementDto) {
        if (userAchievements.contains(achievementDto)) {
            Icon icon = new Icon(VaadinIcon.CHECK_CIRCLE_O);
            icon.setColor(APP_COLOR);
            return icon;
        }
        return new ProgressBar(0, achievementDto.getNumberOfWordsRequired(), progress);
    }

    private Button getDeleteFavoriteWordButton(WordDto wordDto) {
        Button deleteFavorite = getCloseButton();
        deleteFavorite.addThemeVariants(
                ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY
        );
        deleteFavorite.getStyle().set("color", APP_COLOR);

        deleteFavorite.addClickListener(event -> {
            userFacade.removeWordFromFavorites(wordDto.getName());
            favorites.remove(wordDto);
            favoriteWordsGrid.getDataProvider().refreshAll();
        });

        return deleteFavorite;
    }
}
