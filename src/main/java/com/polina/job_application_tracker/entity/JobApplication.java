package com.polina.job_application_tracker.entity;


import com.polina.job_application_tracker.enums.ApplicationStatus;
import jakarta.persistence.*;
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
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;

    private String role;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate appliedAt;

    private String notes;
}
