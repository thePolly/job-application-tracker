package com.polina.job_application_tracker.controller;

import com.polina.job_application_tracker.enums.ApplicationEventType;
import com.polina.job_application_tracker.repository.ApplicationEventRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {

    private final ApplicationEventRepository eventRepository;

    public StatisticsController(ApplicationEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/year/{year}")
    public Map<String, Long> getYearStatistics(@PathVariable int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        Map<String, Long> statistics = new LinkedHashMap<>();
        statistics.put("applications", eventRepository.countByTypeAndEventDateBetween(
                ApplicationEventType.APPLIED,
                startDate,
                endDate
        ));
        statistics.put("interviews", eventRepository.countByTypeInAndEventDateBetween(
                List.of(
                        ApplicationEventType.HR_INTERVIEW,
                        ApplicationEventType.TECH_INTERVIEW,
                        ApplicationEventType.FINAL_INTERVIEW
                ),
                startDate,
                endDate
        ));
        statistics.put("offers", eventRepository.countByTypeAndEventDateBetween(
                ApplicationEventType.OFFER,
                startDate,
                endDate
        ));
        statistics.put("rejections", eventRepository.countByTypeAndEventDateBetween(
                ApplicationEventType.REJECTED,
                startDate,
                endDate
        ));

        return statistics;
    }

    @GetMapping("/monthly/{year}")
    public List<MonthlyApplications> getMonthlyApplications(@PathVariable int year) {
        List<MonthlyApplications> monthlyApplications = new ArrayList<>();

        for (int monthNumber = 1; monthNumber <= 12; monthNumber++) {
            LocalDate startDate = LocalDate.of(year, monthNumber, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            String monthName = Month.of(monthNumber).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            long applications = eventRepository.countByTypeAndEventDateBetween(
                    ApplicationEventType.APPLIED,
                    startDate,
                    endDate
            );

            monthlyApplications.add(new MonthlyApplications(monthName, applications));
        }

        return monthlyApplications;
    }

    public record MonthlyApplications(String month, long applications) {
    }
}
