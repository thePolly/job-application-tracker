package com.polina.job_application_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.job_application_tracker.entity.JobApplication;
import com.polina.job_application_tracker.enums.ApplicationStatus;
import com.polina.job_application_tracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @Test
    void addsJobApplication() throws Exception {
        JobApplication savedApplication = new JobApplication(
                1L,
                "Acme",
                "Backend Engineer",
                ApplicationStatus.APPLIED,
                LocalDate.of(2026, 5, 13),
                "Initial application"
        );

        when(repository.save(any(JobApplication.class))).thenReturn(savedApplication);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JobApplication(
                                null,
                                "Acme",
                                "Backend Engineer",
                                ApplicationStatus.APPLIED,
                                LocalDate.of(2026, 5, 13),
                                "Initial application"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/applications/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.company").value("Acme"))
                .andExpect(jsonPath("$.role").value("Backend Engineer"))
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.appliedAt").value("2026-05-13"))
                .andExpect(jsonPath("$.notes").value("Initial application"));
    }

    @Test
    void rejectsJobApplicationWithoutRequiredFields() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appliedAt": "2026-05-13",
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
}
