package com.gymmer.gymmerstation.programManagement.validations;

import com.gymmer.gymmerstation.domain.Program;

import java.util.List;

public class DivisionValidation {
    private static final String NO_DIVISION = "No Division Found!";
    private static final String NO_EXERCISE_IN_DIVISION = "No Exercise Found In Division ";

    public static void noDivisionValidation(List<Integer> divisionList) {
        if (divisionList.isEmpty()) {
            throw new IllegalArgumentException(NO_DIVISION);
        }
    }

    public static boolean emptyDivisionValidation(Program program, Long division) {
        if(program.getExerciseByDivision(division).isEmpty()) {
            return true;
        }
        return false;
    }

    public static void noExerciseValidation(Program program, List<Integer> divisionList) {
        for (int currentDivision : divisionList) {
            if (program.getExerciseByDivision((long)currentDivision).isEmpty()) {
                throw new IllegalArgumentException(NO_EXERCISE_IN_DIVISION + currentDivision + "!");
            }
        }
    }
}
