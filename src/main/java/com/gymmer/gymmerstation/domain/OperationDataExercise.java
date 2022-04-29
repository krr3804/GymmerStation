package com.gymmer.gymmerstation.domain;

public class OperationDataExercise {
    private String name;
    private Long currentSet;
    private Long rep;
    private Long weight;
    private String restTime;
    private String timeConsumed;


    public OperationDataExercise(String name, Long currentSet, Long rep, Long weight, String restTime, String timeConsumed) {
        this.name = name;
        this.currentSet = currentSet;
        this.rep = rep;
        this.weight = weight;
        this.restTime = restTime;
        this.timeConsumed = timeConsumed;
    }

    public String getName() {
        return name;
    }

    public Long getRep() {
        return rep;
    }

    public Long getWeight() {
        return weight;
    }

    public String getRestTime() {
        return restTime;
    }

    public Long getCurrentSet() {
        return currentSet;
    }

    public String getTimeConsumed() {
        return timeConsumed;
    }
}
