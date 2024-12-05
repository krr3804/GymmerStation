package com.gymmer.gymmerstation.home;

import com.gymmer.gymmerstation.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.home.User.*;
import static com.gymmer.gymmerstation.util.Alerts.generateExitProgramAlert;
import static com.gymmer.gymmerstation.util.Alerts.handleCloseWindowAction;
import static com.gymmer.gymmerstation.util.Util.loadStage;

public class MainController implements Initializable {
    private Socket socket;
    @FXML
    private Label userLabel;

    @FXML
    private Button btnCreateProgram, btnLoadProgram, btnPerformanceArchive, btnExit, btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
            Platform.runLater(() -> {
                viewLogInPage();
            });
        } else {
            socket = User.socket;
        }
        showLoginStatus();
        btnLogout.setOnAction(event -> handleBtnLogoutEvent());
        btnCreateProgram.setOnAction(event -> loadStage("fxml files/create-program-view.fxml",btnCreateProgram.getScene()));
        btnLoadProgram.setOnAction(event -> loadStage("fxml files/load-program-view.fxml",btnLoadProgram.getScene()));
        btnPerformanceArchive.setOnAction(event -> loadStage("fxml files/performance-archive-list-view.fxml",btnPerformanceArchive.getScene()));
        btnExit.setOnAction(event -> handleBtnExitEvent());
    }

    private void handleBtnExitEvent() {
        Optional<ButtonType> result = generateExitProgramAlert().showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(null);
                oos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            System.exit(0);
        }
    }

    private void showLoginStatus() {
        if (user_id != null) {
            userLabel.setText(user_id);
            btnLogout.setText("Log Out");
            btnLogout.setDisable(false);
        } else {
            userLabel.setText("");
            btnLogout.setText("");
            btnLogout.setDisable(true);
        }
    }

    private void handleBtnLogoutEvent() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            HashMap<String, String> map = new HashMap<>();
            map.put("logOut",user_id);
            oos.writeObject(map);
            oos.flush();
            setUser_id(null);
            showLoginStatus();
            viewLogInPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewLogInPage() {
        Stage primaryStage = (Stage) btnCreateProgram.getScene().getWindow();
        Stage popup = new Stage();
        popup.initOwner(primaryStage);
        popup.initModality(Modality.WINDOW_MODAL);
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("fxml files/login-view.fxml"));
            popup.setScene(new Scene(root));
            popup.setTitle("Log In");
            popup.getIcons().add(new Image("file:src/main/resources/com/gymmer/gymmerstation/images/dumbbell.png"));
            popup.setResizable(false);
            handleCloseWindowAction(popup);
            popup.showAndWait();
            showLoginStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

