package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.OperationDataExercise;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.home.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgramOperationServiceImplTest {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private HashMap<String,Object> map;
    private Program registeredProgram1;
    private Program registeredProgram2;

    @BeforeEach
    void setUp() {
        if(!User.socketConnect) {
            final String SERVER_IP = "192.168.137.1";
            final int SERVER_HOST = 8080;
            socket = new Socket();

            try {
                socket.connect(new InetSocketAddress(SERVER_IP, SERVER_HOST));
                System.out.println("Connection Success!");
                User.setSocketConnect(true);
                User.setSocket(socket);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
        }
        socket = User.socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("checkAlreadyLoggedIn", "krr3804");
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int result = (int) ois.readObject();
            if (result == 1) {
                User.setUser_id("krr3804");
            } else {
                throw new IllegalArgumentException("Login Fail");
            }
        } catch (IOException | ClassNotFoundException | IllegalArgumentException exception) {
            exception.printStackTrace();
        }


        List<Exercise> exercises1 = new ArrayList<>();
        exercises1.add(new Exercise("Back Squat",1L,"Fixed","00:05",1L));
        exercises1.add(new Exercise("Split Squat",1L,"Fixed","00:03",2L));
        List<Exercise> exercises2 = new ArrayList<>();
        exercises2.add(new Exercise("Bench Press",1L,"Fixed","00:02",1L));
        exercises2.add(new Exercise("Dumbbell Press",1L,"Fixed","00:04",2L));
        Program program1 = new Program(null,"Leg Buster","To increase leg weight",2L,2L,exercises1);
        Program program2 = new Program(null,"Chest Focused","To increase chest mobility",2L,2L,exercises2);
        Long program1Id = sendProgramAdditionRequest(program1);
        Long program2Id = sendProgramAdditionRequest(program2);
        registeredProgram1 = new Program(program1Id,"Leg Buster","To increase leg weight",2L,2L,exercises1);
        registeredProgram2 = new Program(program2Id,"Chest Focused","To increase chest mobility",2L,2L,exercises2);
    }

    @AfterEach
    void tearDown() {
        sendClearDataRequest(registeredProgram1.getId());
        sendClearDataRequest(registeredProgram2.getId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(null);
            oos.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                User.setUser_id(null);
                User.setSocket(null);
                User.setSocketConnect(false);
                if (ois != null) {
                    ois.close();
                }
                if (oos != null) {
                    oos.close();
                }
                socket.close();
                socket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private Long sendProgramAdditionRequest(Program program) {
        Long programId = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("addProgram",program);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            programId = (Long) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return programId;
    }

    private void sendClearDataRequest(Long programId) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("deleteProgram",programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int success = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int sendAddODPRequest(OperationDataProgram odp) {
        int result = 0;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("savePerformanceData", odp);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            result = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int sendDeleteODPRequest(Long programId) {
        int result = 0;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("deletePerformanceData", programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            result = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Long sendTerminateProgramRequest(Long programId) {
        Long terminatedProgramId = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("terminateProgram", programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            terminatedProgramId = (Long) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return terminatedProgramId;
    }

    @Test
    void savePerformanceData() {
        List<OperationDataExercise> odeList = new ArrayList<>();
        odeList.add(new OperationDataExercise("Back Squat", 1L, 10L, "100","00:05","01:05"));
        OperationDataProgram odp = new OperationDataProgram(registeredProgram1,1L,1L,odeList);
        int res = sendAddODPRequest(odp);
        sendDeleteODPRequest(registeredProgram1.getId());
        assertEquals(1, res);
    }

    @Test
    void terminateProgram() {
        List<OperationDataExercise> odeList = new ArrayList<>();
        odeList.add(new OperationDataExercise("Back Squat", 1L, 10L, "100","00:05","01:05"));
        OperationDataProgram odp = new OperationDataProgram(registeredProgram1,1L,1L,odeList);
        sendAddODPRequest(odp);
        Long terminatedProgramId = sendTerminateProgramRequest(registeredProgram1.getId());
        sendDeleteODPRequest(terminatedProgramId);
        assertNotNull(terminatedProgramId);
        assertNotEquals(registeredProgram1.getId(),terminatedProgramId);
    }

    @Test
    void deletePerformanceData() {
        List<OperationDataExercise> odeList = new ArrayList<>();
        odeList.add(new OperationDataExercise("Back Squat", 1L, 10L, "100","00:05","01:05"));
        OperationDataProgram odp = new OperationDataProgram(registeredProgram1,1L,1L,odeList);
        int res = 0;
        //delete program in progress
        sendAddODPRequest(odp);
        res = sendDeleteODPRequest(registeredProgram1.getId());
        assertEquals(1,res);

        res = 0;
        //delete terminated program
        sendAddODPRequest(odp);
        Long terminatedProgramId = sendTerminateProgramRequest(registeredProgram1.getId());
        res = sendDeleteODPRequest(terminatedProgramId);
        assertEquals(2, res);
    }

    @Test
    void getPerformanceDataList() {
        List<OperationDataExercise> odeList1 = new ArrayList<>();
        odeList1.add(new OperationDataExercise("Back Squat", 1L, 10L, "100","00:05","01:05"));
        OperationDataProgram odp1 = new OperationDataProgram(registeredProgram1,1L,1L,odeList1);
        List<OperationDataExercise> odeList2 = new ArrayList<>();
        odeList2.add(new OperationDataExercise("Split Squat",1L,10L,"100","00:03","02:05"));
        OperationDataProgram odp2 = new OperationDataProgram(registeredProgram1,1L,2L,odeList2);
        sendAddODPRequest(odp1);
        sendAddODPRequest(odp2);
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getPerformanceDataList", registeredProgram1);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            List<OperationDataProgram> odpList = (List<OperationDataProgram>) ois.readObject();
            assertEquals(2,odpList.size());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sendDeleteODPRequest(registeredProgram1.getId());
        }
    }

    @Test
    void getProgress() {
        List<OperationDataExercise> odeList1 = new ArrayList<>();
        odeList1.add(new OperationDataExercise("Back Squat", 1L, 10L, "100","00:05","01:05"));
        OperationDataProgram odp1 = new OperationDataProgram(registeredProgram1,1L,1L,odeList1);
        List<OperationDataExercise> odeList2 = new ArrayList<>();
        odeList2.add(new OperationDataExercise("Split Squat",1L,10L,"100","00:03","02:05"));
        OperationDataProgram odp2 = new OperationDataProgram(registeredProgram1,1L,2L,odeList2);
        sendAddODPRequest(odp1);
        sendAddODPRequest(odp2);

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getProgress", registeredProgram1.getId());
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int progress = (int) ois.readObject();
            assertEquals(2,progress);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sendDeleteODPRequest(registeredProgram1.getId());
        }
    }

    @Test
    void getProgramsInArchiveList() {
        List<OperationDataExercise> odeList1 = new ArrayList<>();
        odeList1.add(new OperationDataExercise("Back Squat", 1L, 10L, "100","00:05","01:05"));
        OperationDataProgram odp1 = new OperationDataProgram(registeredProgram1,1L,1L,odeList1);
        List<OperationDataExercise> odeList2 = new ArrayList<>();
        odeList2.add(new OperationDataExercise("Bench Press",1L,10L,"60","00:02","00:10"));
        OperationDataProgram odp2 = new OperationDataProgram(registeredProgram2,1L,2L,odeList2);
        sendAddODPRequest(odp1);
        sendAddODPRequest(odp2);
        Long terminatedProgramId = sendTerminateProgramRequest(registeredProgram1.getId());

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getProgramsInArchive",true);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            List<Program> programs = (List<Program>) ois.readObject();
            assertEquals(1,programs.size());
            assertEquals(terminatedProgramId,programs.get(0).getId());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sendDeleteODPRequest(registeredProgram1.getId());
            sendDeleteODPRequest(registeredProgram2.getId());
            sendDeleteODPRequest(terminatedProgramId);
        }
    }
}