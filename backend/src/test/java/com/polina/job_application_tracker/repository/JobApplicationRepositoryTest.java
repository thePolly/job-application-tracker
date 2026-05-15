package com.polina.job_application_tracker.repository;

import com.polina.job_application_tracker.entity.JobApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository repository;

    @Test
    void savesAndFindsJobApplicationById() {
        JobApplication application = new JobApplication(
                null,
                "Acme",
                "Backend Engineer",
                "Initial application"
        );

        JobApplication savedApplication = repository.save(application);

        assertThat(repository.findById(savedApplication.getId()))
                .isPresent()
                .get()
                .satisfies(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getCompany()).isEqualTo("Acme");
                    assertThat(saved.getRole()).isEqualTo("Backend Engineer");
                });
    }
}
