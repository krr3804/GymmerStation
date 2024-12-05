package com.gymmer.gymmerstation.programManagement;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.home.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.gymmer.gymmerstation.home.User.user_id;

public class ProgramServiceImpl implements ProgramService {
    private final Socket socket = User.socket;
    private HashMap<String,Object> map;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    @Override
    public void addProgram(Program program) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("addProgram",program);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            Long programId = (Long) ois.readObject();
            if (programId == null) {
                throw new IllegalArgumentException("Program Save Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Program> getProgramList() {
        List<Program> programList = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("getProgramList",User.user_id);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            programList = (List<Program>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return programList;
    }

    @Override
    public void editProgram(Program newProgram) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("editProgram",newProgram);
            oos.writeObject(map);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replaceExercises(Program newProgram) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("replaceExercises",newProgram);
            oos.writeObject(map);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProgram(Long programId) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            map = new HashMap<>();
            map.put("deleteProgram",programId);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int success = (int) ois.readObject();
            if(success == 0) {
                throw new IllegalArgumentException("Program Deletion Failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
