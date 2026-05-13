package com.polina.job_application_tracker.repository;

import com.polina.job_application_tracker.entity.JobApplication;
import com.polina.job_application_tracker.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);
}
