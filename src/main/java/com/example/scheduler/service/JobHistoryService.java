package com.example.scheduler.service;

import com.example.scheduler.entity.JobHistory;
import com.example.scheduler.repository.JobHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobHistoryService {

    private final JobHistoryRepository jobHistoryRepository;
// wsl --update
// wsl --install -d Ubuntu
    public List<JobHistory> getAll() {
        return jobHistoryRepository.findAll();
    }

    public List<JobHistory> getByJobName(String jobName) {
        return jobHistoryRepository.findByJobNameOrderByStartTimeDesc(jobName);
    }
}