package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;

import java.util.List;

public interface ProgramOperationService {
    void savePerformanceData(OperationDataProgram operationDataProgram);

    void terminateProgram(Long programId);

    void deletePerformanceData(Long programId);

    List<OperationDataProgram> getPerformanceDataList(Program program);

    int getProgress(Long programId);

    Long getCurrentWeek(Program program);

    Long getCurrentDivision(Program program);

    List<Program> getProgramsInArchive(boolean status);
}
