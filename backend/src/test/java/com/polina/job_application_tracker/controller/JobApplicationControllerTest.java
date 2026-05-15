package com.polina.job_application_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.job_application_tracker.entity.ApplicationEvent;
import com.polina.job_application_tracker.entity.JobApplication;
import com.polina.job_application_tracker.enums.ApplicationEventType;
import com.polina.job_application_tracker.repository.ApplicationEventRepository;
import com.polina.job_application_tracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobApplicationController.class)
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JobApplicationRepository repository;

    @MockitoBean
    private ApplicationEventRepository eventRepository;

    @Test
    void addsJobApplication() throws Exception {
        JobApplication savedApplication = new JobApplication(
                1L,
                "Acme",
                "Backend Engineer",
                "Initial application"
        );

        when(repository.save(any(JobApplication.class))).thenReturn(savedApplication);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JobApplication(
                                null,
                                "Acme",
                                "Backend Engineer",
                                "Initial application"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/applications/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.company").value("Acme"))
                .andExpect(jsonPath("$.role").value("Backend Engineer"))
                .andExpect(jsonPath("$.notes").value("Initial application"));
    }

    @Test
    void rejectsJobApplicationWithoutRequiredFields() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "notes": "No required fields"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletesJobApplication() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/applications/1"))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(1L);
    }

    @Test
    void returnsNotFoundWhenDeletingMissingJobApplication() throws Exception {
        when(repository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/applications/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addsApplicationEvent() throws Exception {
        JobApplication application = new JobApplication(
                1L,
                "Acme",
                "Backend Engineer",
                "Initial application"
        );
        ApplicationEvent savedEvent = new ApplicationEvent(
                10L,
                ApplicationEventType.HR_INTERVIEW,
                LocalDate.of(2026, 5, 20),
                "Talked with HR",
                application
        );

        when(repository.findById(1L)).thenReturn(Optional.of(application));
        when(eventRepository.save(any(ApplicationEvent.class))).thenReturn(savedEvent);

        mockMvc.perform(post("/applications/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "HR_INTERVIEW",
                                  "eventDate": "2026-05-20",
                                  "notes": "Talked with HR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.type").value("HR_INTERVIEW"))
                .andExpect(jsonPath("$.eventDate").value("2026-05-20"))
                .andExpect(jsonPath("$.notes").value("Talked with HR"))
                .andExpect(jsonPath("$.jobApplication").doesNotExist());
    }

    @Test
    void returnsEventsForApplication() throws Exception {
        ApplicationEvent event = new ApplicationEvent(
                10L,
                ApplicationEventType.TECH_INTERVIEW,
                LocalDate.of(2026, 5, 21),
                "Java round",
                null
        );

        when(repository.existsById(1L)).thenReturn(true);
        when(eventRepository.findByJobApplicationId(1L)).thenReturn(List.of(event));

        mockMvc.perform(get("/applications/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].type").value("TECH_INTERVIEW"))
                .andExpect(jsonPath("$[0].eventDate").value("2026-05-21"))
                .andExpect(jsonPath("$[0].notes").value("Java round"));
    }
}
