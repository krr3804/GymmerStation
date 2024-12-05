package com.gymmer.gymmerstation.programManagement;

import com.gymmer.gymmerstation.domain.Exercise;
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

class ProgramServiceImplTest {
private Socket socket;
private ObjectOutputStream oos;
private ObjectInputStream ois;
private HashMap<String,Object> map;
    @BeforeEach
    void setUp() {
        if(!User.socketConnect) {
            final String SERVER_IP = "192.168.200.134";
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
        User.setUser_id("krr3804");
    }

    @AfterEach
    void tearDown() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addProgram() {
        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Back Squat",1L,"Fixed","00:05",1L));
        exerciseList.add(new Exercise("Split Squat",1L,"Fixed","00:03",2L));
        Program program = new Program(null,"Leg Buster","To increase leg weight",2L,2L,exerciseList);

        Long programId = sendProgramAdditionRequest(program);
        assertNotNull(programId);
        sendClearDataRequest(programId);
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

    private int sendClearDataRequest(Long programId) {
        int success = 0;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("deleteProgram",programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            success = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Test
    void getProgramList() {
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

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getProgramList",User.user_id);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            List<Program> programs = (ArrayList<Program>) ois.readObject();
            assertEquals(2,programs.size());
            assertEquals(program1Id,programs.get(0).getId());
            assertEquals(program2Id,programs.get(1).getId());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sendClearDataRequest(program1Id);
            sendClearDataRequest(program2Id);
        }
    }

    @Test
    void editProgram() {
        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Back Squat",1L,"Fixed","00:05",1L));
        exerciseList.add(new Exercise("Split Squat",1L,"Fixed","00:03",1L));
        exerciseList.add(new Exercise("Leg Press",1L,"Fixed","00:02",2L));
        Program program = new Program(null,"Leg Buster","To increase leg weight",2L,2L,exerciseList);
        Long programId = sendProgramAdditionRequest(program);
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            Program editedProgram = new Program(programId,"Leg Training","To increase leg mobility",3L,3L);
            map = new HashMap<>();
            map.put("editProgram",editedProgram);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            Program alteredProgram = (Program) ois.readObject();
            assertNotEquals(program.getName(),alteredProgram.getName());
            assertNotEquals(program.getPurpose(),alteredProgram.getPurpose());
            assertNotEquals(program.getLength(),alteredProgram.getLength());
            assertNotEquals(program.getDivisionQty(),alteredProgram.getDivisionQty());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            List<Exercise> additionList = new ArrayList<>();
            additionList.add(new Exercise("Front Squat",1L,"Fixed","00:06",3L));
            HashMap<Long, List<Exercise>> hashMap = new HashMap<>();
            hashMap.put(programId,additionList);
            map = new HashMap<>();
            map.put("addExercises",hashMap);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int result = (int)ois.readObject();
            assertEquals(1,result);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            List<Exercise> deletionList = new ArrayList<>();
            deletionList.add(new Exercise("Back Squat",1L,"Fixed","00:05",1L));
            HashMap<Long, List<Exercise>> hashMap = new HashMap<>();
            hashMap.put(programId,deletionList);
            map = new HashMap<>();
            map.put("deleteExercises",hashMap);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int result = (int)ois.readObject();
            assertEquals(1,result);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sendClearDataRequest(programId);
        }
    }

    @Test
    void removeEntireDivision() {
        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Back Squat",1L,"Fixed","00:05",1L));
        exerciseList.add(new Exercise("Split Squat",1L,"Fixed","00:03",1L));
        exerciseList.add(new Exercise("Leg Press",1L,"Fixed","00:02",2L));
        Program program = new Program(null,"Leg Buster","To increase leg weight",2L,2L,exerciseList);
        Long programId = sendProgramAdditionRequest(program);
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            String data = programId + "," + 1;
            map.put("removeEntireDivision",data);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int result = (int)ois.readObject();
            assertEquals(4,result);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sendClearDataRequest(programId);
        }
    }

    @Test
    void deleteProgram() {
        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Back Squat",1L,"Fixed","00:05",1L));
        exerciseList.add(new Exercise("Split Squat",1L,"Fixed","00:03",2L));
        Program program = new Program(null,"Leg Buster","To increase leg weight",2L,2L,exerciseList);
        Long programId = sendProgramAdditionRequest(program);
        int success = sendClearDataRequest(programId);
        assertEquals(3,success);
    }
}