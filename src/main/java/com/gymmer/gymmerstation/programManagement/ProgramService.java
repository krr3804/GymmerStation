package com.gymmer.gymmerstation.programManagement;

import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.Program;

import java.util.List;

public interface ProgramService {
    void addProgram(Program program);

    List<Program> getProgramList();

    void editProgram(Program newProgram);

    void replaceExercises(Program newProgram);

    void deleteProgram(Long programId);
}
