package com.example.scheduler.controller;

import com.example.scheduler.dto.JobRequest;
import com.example.scheduler.dto.JobResponse;
import com.example.scheduler.entity.JobHistory;
import com.example.scheduler.service.JobHistoryService;
import com.example.scheduler.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Управление фоновыми задачами")
public class JobController {

    private final JobService jobService;
    private final JobHistoryService jobHistoryService;

    @PostMapping
    @Operation(summary = "Создать задачу")
    public ResponseEntity<String> createJob(@RequestBody JobRequest request) throws Exception {
        jobService.createJob(request);
        return ResponseEntity.ok("Job created: " + request.getJobName());
    }

    @GetMapping
    @Operation(summary = "Получить все задачи")
    public ResponseEntity<List<JobResponse>> getAllJobs() throws Exception {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{jobGroup}/{jobName}")
    @Operation(summary = "Получить задачу")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable String jobName,
            @PathVariable String jobGroup) throws Exception {
        return ResponseEntity.ok(jobService.getJob(jobName, jobGroup));
    }

    @PutMapping("/{jobGroup}/{jobName}")
    @Operation(summary = "Изменить расписание")
    public ResponseEntity<String> updateJob(
            @PathVariable String jobName,
            @PathVariable String jobGroup,
            @RequestParam String cronExpression) throws Exception {
        jobService.updateJob(jobName, jobGroup, cronExpression);
        return ResponseEntity.ok("Job updated: " + jobName);
    }

    @DeleteMapping("/{jobGroup}/{jobName}")
    @Operation(summary = "Удалить задачу")
    public ResponseEntity<String> deleteJob(
            @PathVariable String jobName,
            @PathVariable String jobGroup) throws Exception {
        jobService.deleteJob(jobName, jobGroup);
        return ResponseEntity.ok("Job deleted: " + jobName);
    }

    @PostMapping("/{jobGroup}/{jobName}/run")
    @Operation(summary = "Ручной запуск задачи")
    public ResponseEntity<String> triggerJob(
            @PathVariable String jobName,
            @PathVariable String jobGroup) throws Exception {
        jobService.triggerJob(jobName, jobGroup);
        return ResponseEntity.ok("Job triggered: " + jobName);
    }

    @PatchMapping("/{jobGroup}/{jobName}/toggle")
    @Operation(summary = "Включить/выключить задачу")
    public ResponseEntity<String> toggleJob(
            @PathVariable String jobName,
            @PathVariable String jobGroup,
            @RequestParam boolean enable) throws Exception {
        if (enable) {
            jobService.resumeJob(jobName, jobGroup);
        } else {
            jobService.pauseJob(jobName, jobGroup);
        }
        return ResponseEntity.ok("Job " + (enable ? "resumed" : "paused") + ": " + jobName);
    }

    @GetMapping("/history")
    @Operation(summary = "История всех задач")
    public ResponseEntity<List<JobHistory>> getAllHistory() {
        return ResponseEntity.ok(jobHistoryService.getAll());
    }

    @GetMapping("/history/{jobName}")
    @Operation(summary = "История конкретной задачи")
    public ResponseEntity<List<JobHistory>> getJobHistory(@PathVariable String jobName) {
        return ResponseEntity.ok(jobHistoryService.getByJobName(jobName));
    }
}