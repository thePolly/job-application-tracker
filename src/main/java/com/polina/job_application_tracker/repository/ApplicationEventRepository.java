package com.polina.job_application_tracker.repository;

import com.polina.job_application_tracker.entity.ApplicationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, Long> {

    List<ApplicationEvent> findByJobApplicationId(Long jobApplicationId);
}
