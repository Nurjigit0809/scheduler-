package com.example.scheduler.service;

import com.example.scheduler.dto.JobRequest;
import com.example.scheduler.dto.JobResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final Scheduler scheduler;

    @SuppressWarnings("unchecked")
    public void createJob(JobRequest request) throws Exception {
        Class<? extends Job> jobClass =
                (Class<? extends Job>) Class.forName(request.getJobClass());

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(request.getJobName(), request.getJobGroup())
                .withDescription(request.getDescription())
                .usingJobData(new JobDataMap(request.getParams() != null ? request.getParams() : new java.util.HashMap<>()))
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(request.getJobName(), request.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(request.getCronExpression()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Job created: {}", request.getJobName());
    }

    public List<JobResponse> getAllJobs() throws Exception {
        List<JobResponse> jobs = new ArrayList<>();

        for (JobKey key : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
            jobs.add(buildJobResponse(key));
        }
        return jobs;
    }

    public JobResponse getJob(String jobName, String jobGroup) throws Exception {
        JobKey key = new JobKey(jobName, jobGroup);
        return buildJobResponse(key);
    }

    public void updateJob(String jobName, String jobGroup, String cronExpression) throws Exception {
        TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);

        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);
        log.info("Job updated: {}", jobName);
    }

    public void deleteJob(String jobName, String jobGroup) throws Exception {
        scheduler.deleteJob(new JobKey(jobName, jobGroup));
        log.info("Job deleted: {}", jobName);
    }

    public void triggerJob(String jobName, String jobGroup) throws Exception {
        scheduler.triggerJob(new JobKey(jobName, jobGroup));
        log.info("Job triggered manually: {}", jobName);
    }

    public void pauseJob(String jobName, String jobGroup) throws Exception {
        scheduler.pauseJob(new JobKey(jobName, jobGroup));
        log.info("Job paused: {}", jobName);
    }

    public void resumeJob(String jobName, String jobGroup) throws Exception {
        scheduler.resumeJob(new JobKey(jobName, jobGroup));
        log.info("Job resumed: {}", jobName);
    }

    private JobResponse buildJobResponse(JobKey key) throws Exception {
        JobDetail jobDetail = scheduler.getJobDetail(key);
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);

        JobResponse response = new JobResponse();
        response.setJobName(key.getName());
        response.setJobGroup(key.getGroup());
        response.setJobClass(jobDetail.getJobClass().getName());
        response.setDescription(jobDetail.getDescription());

        if (!triggers.isEmpty()) {
            Trigger trigger = triggers.get(0);
            if (trigger instanceof CronTrigger cronTrigger) {
                response.setCronExpression(cronTrigger.getCronExpression());
            }
            if (trigger.getNextFireTime() != null)
                response.setNextFireTime(trigger.getNextFireTime().toString());
            if (trigger.getPreviousFireTime() != null)
                response.setPreviousFireTime(trigger.getPreviousFireTime().toString());

            Trigger.TriggerState state = scheduler.getTriggerState(trigger.getKey());
            response.setStatus(state.name());
        }

        return response;
    }
}