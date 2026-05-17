package com.polina.job_application_tracker.controller;

import com.polina.job_application_tracker.enums.ApplicationEventType;
import com.polina.job_application_tracker.repository.ApplicationEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationEventRepository eventRepository;

    @Test
    void returnsStatisticsForYear() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        when(eventRepository.countByTypeAndEventDateBetween(ApplicationEventType.APPLIED, startDate, endDate))
                .thenReturn(52L);
        when(eventRepository.countByTypeInAndEventDateBetween(
                List.of(
                        ApplicationEventType.HR_INTERVIEW,
                        ApplicationEventType.TECH_INTERVIEW,
                        ApplicationEventType.FINAL_INTERVIEW
                ),
                startDate,
                endDate
        )).thenReturn(11L);
        when(eventRepository.countByTypeAndEventDateBetween(ApplicationEventType.OFFER, startDate, endDate))
                .thenReturn(2L);
        when(eventRepository.countByTypeAndEventDateBetween(ApplicationEventType.REJECTED, startDate, endDate))
                .thenReturn(31L);

        mockMvc.perform(get("/statistics/year/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applications").value(52))
                .andExpect(jsonPath("$.interviews").value(11))
                .andExpect(jsonPath("$.offers").value(2))
                .andExpect(jsonPath("$.rejections").value(31));
    }

    @Test
    void returnsMonthlyApplicationsForYear() throws Exception {
        when(eventRepository.countByTypeAndEventDateBetween(
                ApplicationEventType.APPLIED,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        )).thenReturn(3L);
        when(eventRepository.countByTypeAndEventDateBetween(
                ApplicationEventType.APPLIED,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 28)
        )).thenReturn(7L);
        when(eventRepository.countByTypeAndEventDateBetween(
                ApplicationEventType.APPLIED,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        )).thenReturn(2L);

        mockMvc.perform(get("/statistics/monthly/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].month").value("Jan"))
                .andExpect(jsonPath("$[0].applications").value(3))
                .andExpect(jsonPath("$[1].month").value("Feb"))
                .andExpect(jsonPath("$[1].applications").value(7))
                .andExpect(jsonPath("$[2].month").value("Mar"))
                .andExpect(jsonPath("$[2].applications").value(2))
                .andExpect(jsonPath("$[11].month").value("Dec"))
                .andExpect(jsonPath("$[11].applications").value(0));
    }
}
