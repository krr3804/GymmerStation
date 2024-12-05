package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.domain.OperationDataExercise;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.home.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProgramOperationServiceImpl implements ProgramOperationService{
    private final Socket socket = User.socket;
    private HashMap<String,Object> map;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    @Override
    public void savePerformanceData(OperationDataProgram operationDataProgram) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("savePerformanceData", operationDataProgram);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int result = (int) ois.readObject();
            if(result < 1) {
                throw new IllegalArgumentException("Performance Data Has Not Been Saved!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void terminateProgram(Long programId) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("terminateProgram", programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            Long terminatedProgramId = (Long) ois.readObject();
            if(terminatedProgramId == null) {
                throw new IllegalArgumentException("Program Termination Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePerformanceData(Long programId) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("deletePerformanceData", programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int result = (int) ois.readObject();
            if(result < 1) {
                throw new IllegalArgumentException("Data Deletion Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OperationDataProgram> getPerformanceDataList(Program program) {
        List<OperationDataProgram> odpList = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getPerformanceDataList", program);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            odpList = (List<OperationDataProgram>) ois.readObject();
            if (odpList == null) {
                throw new IllegalArgumentException("Data Loading Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return odpList;
    }

    @Override
    public int getProgress(Long programId) {
        int progress = -1;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getProgress", programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            progress = (int) ois.readObject();
            if(progress == -1) {
                throw new IllegalArgumentException("Data Loading Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return progress;
    }

    @Override
    public Long getCurrentWeek(Program program) {
        long res = 0L;
        try {
            res = getProgress(program.getId()) / program.getDivisionQty() + 1;
        } catch (Exception e) {
            System.err.println("error : " + e.getMessage());
        }
        return res;
    }

    @Override
    public Long getCurrentDivision(Program program) {
        long res = 0L;
        try {
            res = getProgress(program.getId()) % program.getDivisionQty() + 1;
        } catch (Exception e) {
            System.err.println("error : " + e.getMessage());
        }
        return res;
    }

    @Override
    public List<Program> getProgramsInArchive(boolean status) {
        List<Program> programs = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getProgramsInArchive",status);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            programs = (List<Program>) ois.readObject();
            if (programs == null) {
                throw new IllegalArgumentException("Data Loading Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return programs;
    }
}
