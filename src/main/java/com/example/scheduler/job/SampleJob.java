package com.example.scheduler.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

@Slf4j
public class SampleJob extends BaseJob {

    @Override
    protected void executeJob(JobExecutionContext context) throws Exception {
        String message = context.getJobDetail()
                .getJobDataMap()
                .getString("message");

        log.info("SampleJob executing with message: {}", message);

        // Имитация работы
        Thread.sleep(1000);

        log.info("SampleJob finished!");
    }
}