package com.polina.job_application_tracker.controller;


import com.polina.job_application_tracker.entity.ApplicationEvent;
import com.polina.job_application_tracker.entity.JobApplication;
import com.polina.job_application_tracker.repository.ApplicationEventRepository;
import com.polina.job_application_tracker.repository.JobApplicationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationRepository repository;
    private final ApplicationEventRepository eventRepository;

    public JobApplicationController(JobApplicationRepository repository, ApplicationEventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<JobApplication> getAllApplications() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<JobApplication> addApplication(@Valid @RequestBody JobApplication application) {
        JobApplication savedApplication = repository.save(application);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedApplication.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<ApplicationEvent> addEvent(
            @PathVariable Long id,
            @RequestBody ApplicationEvent event
    ) {
        return repository.findById(id)
                .map(application -> {
                    event.setJobApplication(application);
                    ApplicationEvent savedEvent = eventRepository.save(event);
                    return ResponseEntity.ok(savedEvent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<ApplicationEvent>> getEvents(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(eventRepository.findByJobApplicationId(id));
    }
}
