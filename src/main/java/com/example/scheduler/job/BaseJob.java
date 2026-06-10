package com.example.scheduler.job;

import com.example.scheduler.entity.JobHistory;
import com.example.scheduler.repository.JobHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Slf4j
public abstract class BaseJob implements Job {

    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        String jobGroup = context.getJobDetail().getKey().getGroup();

        log.info("Job started: {}", jobName);

        JobHistory history = new JobHistory();
        history.setJobName(jobName);
        history.setJobGroup(jobGroup);
        history.setStartTime(LocalDateTime.now());

        try {
            executeJob(context);
            history.setStatus("SUCCESS");
            log.info("Job completed successfully: {}", jobName);
        } catch (Exception e) {
            history.setStatus("FAILED");
            history.setErrorMessage(e.getMessage());
            log.error("Job failed: {} - {}", jobName, e.getMessage());
            throw new JobExecutionException(e);
        } finally {
            history.setEndTime(LocalDateTime.now());
            jobHistoryRepository.save(history);
        }
    }

    protected abstract void executeJob(JobExecutionContext context) throws Exception;
}