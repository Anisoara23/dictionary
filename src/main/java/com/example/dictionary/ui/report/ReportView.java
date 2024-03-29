package com.example.dictionary.ui.report;

import com.example.dictionary.application.facade.WordFacade;
import com.example.dictionary.application.report.data.WordDetail;
import com.example.dictionary.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.security.RolesAllowed;
import net.sf.dynamicreports.report.exception.DRException;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.dictionary.ui.util.UiUtils.APP_NAME;
import static com.example.dictionary.ui.util.UiUtils.showNotification;
import static com.example.dictionary.ui.util.UiUtils.showSuccess;

@Route(value = "reports", layout = MainLayout.class)
@PageTitle("Reports | " + APP_NAME)
@RolesAllowed("ADMIN")
public class ReportView extends VerticalLayout {

    public final String WORDS_CONTRIBUTIONS_REPORT = getTranslation("reports.words.contributions");

    public final String WORDS_STATISTIC_REPORT = getTranslation("reports.words.statistic");

    private Select<String> selectReportType;

    private Button generateReport;

    private final WordFacade wordFacade;

    private List<WordDetail> wordsDetails;

    private Div reportDescription = new Div();

    private ComboBox<Integer> yearComboBox = new ComboBox<>(getTranslation("year"));

    private ComboBox<Month> monthComboBox = new ComboBox<>(getTranslation("month"));

    private Registration registration;

    private Integer selectedYear;

    private String selectedMonth;

    public ReportView(WordFacade wordFacade) {
        this.wordFacade = wordFacade;
        wordsDetails = wordFacade.getAllWordsDetails();

        generateReport = new Button(getTranslation("reports.generate"));
        reportDescription.setMaxWidth("40%");
        reportDescription.getStyle().set("text-align", "center");

        setupReportTypeSelection();

        HorizontalLayout reportSelectionLayout = new HorizontalLayout(selectReportType, generateReport);
        reportSelectionLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        setHorizontalComponentAlignment(Alignment.CENTER,
                reportSelectionLayout, reportDescription, yearComboBox, monthComboBox
        );
        add(reportSelectionLayout);
    }

    private void setupReportTypeSelection() {
        selectReportType = new Select<>();
        selectReportType.setLabel(getTranslation("reports.select.type"));
        selectReportType.setItems(WORDS_CONTRIBUTIONS_REPORT, WORDS_STATISTIC_REPORT);
        selectReportType.addValueChangeListener(event -> {
            String reportTypeValue = event.getValue();
            cleanupReportConfigurations();
            if (reportTypeValue.equalsIgnoreCase(WORDS_CONTRIBUTIONS_REPORT)) {
                setupWordsContributionsReport();
            } else if (reportTypeValue.equalsIgnoreCase(WORDS_STATISTIC_REPORT)) {
                setupWordsStatisticReport();
            }
        });
    }

    private void cleanupReportConfigurations() {
        if (registration != null) {
            registration.remove();
        }
        reportDescription.removeAll();
        remove(reportDescription, yearComboBox, monthComboBox);
    }

    private void setupWordsContributionsReport() {
        reportDescription.add(getTranslation("reports.words.contribution.description"));

        registration = generateReport.addClickListener(event -> {
            try {
                wordFacade.generateWordsContributionReport();
                showSuccess(getTranslation("reports.success.message"));
            } catch (DRException | JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                     JobParametersInvalidException | JobRestartException | IOException exception) {
                showNotification(exception.getMessage());
            }
        });

        add(reportDescription);
    }

    private void setupWordsStatisticReport() {
        reportDescription.add(getTranslation("reports.words.statistic.description"));

        monthComboBox.setEnabled(false);
        setupYearComboBox();
        registration = generateReport.addClickListener(event -> {
            try {
                wordFacade.generateWordsStatisticsReport(selectedYear, selectedMonth);
                showSuccess(getTranslation("reports.success.message"));
            } catch (DRException | JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                     JobParametersInvalidException | JobRestartException | IOException exception) {
                showNotification(exception.getMessage());
            }
        });

        add(reportDescription, yearComboBox, monthComboBox);
    }

    private void setupYearComboBox() {
        Set<Integer> years = wordsDetails.stream()
                .map(wordDetail -> wordDetail.getAddedAt().getYear())
                .collect(Collectors.toSet());

        yearComboBox.setItems(years);
        yearComboBox.setRequired(true);

        yearComboBox.addValueChangeListener(event -> {
            Integer year = event.getValue();
            selectedYear = year;
            setupMonthComboBox(year);
        });
    }

    private void setupMonthComboBox(Integer year) {
        monthComboBox.setEnabled(true);
        Set<Month> months = wordsDetails.stream()
                .filter(wordDetail -> wordDetail.getAddedAt().getYear() == year)
                .map(wordDetail -> wordDetail.getAddedAt()
                        .getMonth())
                .collect(Collectors.toSet());

        monthComboBox.setItems(months);
        monthComboBox.setItemLabelGenerator(item -> item.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        monthComboBox.setRequired(true);

        monthComboBox.addValueChangeListener(event -> selectedMonth = event.getValue().toString());
    }
}
