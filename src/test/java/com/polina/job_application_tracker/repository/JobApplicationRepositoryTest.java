package com.polina.job_application_tracker.repository;

import com.polina.job_application_tracker.entity.JobApplication;
import com.polina.job_application_tracker.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository repository;

    @Test
    void savesAndFindsJobApplicationByStatus() {
        JobApplication application = new JobApplication(
                null,
                "Acme",
                "Backend Engineer",
                ApplicationStatus.APPLIED,
                LocalDate.of(2026, 5, 12),
                "Initial application"
        );

        repository.save(application);

        assertThat(repository.findByStatus(ApplicationStatus.APPLIED))
                .singleElement()
                .satisfies(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getCompany()).isEqualTo("Acme");
                    assertThat(saved.getRole()).isEqualTo("Backend Engineer");
                });
    }
}
