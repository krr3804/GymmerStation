package com.gymmer.gymmerstation.home;

import com.gymmer.gymmerstation.domain.Program;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class User {
    public static String user_id;
    public static boolean socketConnect = false;
    public static Socket socket;

    public static void setUser_id(String user_id) {
        User.user_id = user_id;
    }

    public static void setSocketConnect(boolean socketConnect) {
        User.socketConnect = socketConnect;
    }

    public static void setSocket(Socket socket) {
        User.socket = socket;
    }
}
