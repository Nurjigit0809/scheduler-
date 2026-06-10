package com.example.scheduler.dto;

import lombok.Data;

import java.util.Map;

@Data
public class JobRequest {

    private String jobName;
    private String jobGroup;
    private String jobClass;
    private String cronExpression;
    private String description;
    private Map<String, Object> params;
}