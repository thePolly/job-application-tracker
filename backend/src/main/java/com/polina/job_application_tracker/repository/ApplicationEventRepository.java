package com.polina.job_application_tracker.repository;

import com.polina.job_application_tracker.entity.ApplicationEvent;
import com.polina.job_application_tracker.enums.ApplicationEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, Long> {

    List<ApplicationEvent> findByJobApplicationId(Long jobApplicationId);

    long countByTypeAndEventDateBetween(ApplicationEventType type, LocalDate startDate, LocalDate endDate);

    long countByTypeInAndEventDateBetween(Collection<ApplicationEventType> types, LocalDate startDate, LocalDate endDate);
}
