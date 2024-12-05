package com.gymmer.gymmerstation.programManagement.validations;

import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.Program;

import java.util.List;

public class ProgramEditionValidation {
    public static void checkCurrentProgress(int currentProgress, Long length, Long division) {
        if(currentProgress >= length * division) {
            throw new IllegalArgumentException("Please Set Total Length Larger Than " + currentProgress + "!");
        }
    }

    public static boolean checkChangeInProgramContents(Program program, String inpName, String inpPurpose, Long inpLength) {
        return !program.getName().equals(inpName) || !program.getPurpose().equals(inpPurpose) ||
                !program.getLength().equals(inpLength) || !program.getDivisionQty().equals(program.countDivision());
    }

    public static boolean checkChangeInExerciseList(List<Exercise> oldExerciseList, List<Exercise> newExerciseList) {
        return !newExerciseList.containsAll(oldExerciseList) || !oldExerciseList.containsAll(newExerciseList);
    }
}
