package com.gymmer.gymmerstation.programManagement.validations;

import com.gymmer.gymmerstation.domain.Exercise;

import java.util.List;

public class DuplicateExerciseValidation {
    private static final String DUPLICATE_EXERCISE = "Exercise Already In The List!";
    public static void duplicateExerciseValidation(List<Exercise> list, String name) {
        if(list.stream().anyMatch(exercise -> exercise.getName().equals(name))) {
            throw new IllegalArgumentException(DUPLICATE_EXERCISE);
        }
    }
}
