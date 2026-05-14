package com.polina.job_application_tracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.polina.job_application_tracker.enums.ApplicationEventType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "application_events")
public class ApplicationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationEventType type;

    private LocalDate eventDate;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "job_application_id")
    @JsonBackReference
    private JobApplication jobApplication;
}
