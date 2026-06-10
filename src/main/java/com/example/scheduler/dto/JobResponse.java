package com.example.scheduler.dto;

import lombok.Data;

@Data
public class JobResponse {

    private String jobName;
    private String jobGroup;
    private String jobClass;
    private String cronExpression;
    private String description;
    private String status;
    private String nextFireTime;
    private String previousFireTime;
}