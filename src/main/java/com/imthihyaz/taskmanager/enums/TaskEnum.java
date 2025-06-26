package com.imthihyaz.taskmanager.enums;

import java.util.Optional;

public enum TaskEnum {
    ALL,ASSIGNED,UNASSIGNED;

    public Optional<TaskEnum> fromString(String type){
        try {
            return Optional.of(TaskEnum.valueOf(type.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
