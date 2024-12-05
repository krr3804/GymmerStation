package com.gymmer.gymmerstation.home;

import com.gymmer.gymmerstation.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.home.validations.PasswordNotMatchValidation.checkPasswordMismatch;
import static com.gymmer.gymmerstation.home.validations.RegisterInputValidation.checkPassword;
import static com.gymmer.gymmerstation.home.validations.RegisterInputValidation.checkUserId;
import static com.gymmer.gymmerstation.util.Alerts.generateErrorAlert;
import static com.gymmer.gymmerstation.util.Alerts.generateInformationAlert;
import static com.gymmer.gymmerstation.util.Util.loadStage;

public class RegisterController implements Initializable {
    @FXML
    private TextField userId, password, confirmPassword;

    @FXML
    private Button btnCheckID, btnReturn, btnRegister;
    private HashMap<String, String> map;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private int checkNum = -1;
    private Socket socket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        socket = User.socket;
        btnCheckID.setOnAction(event -> checkValidations(event));
        btnRegister.setOnAction(event -> checkValidations(event));
        btnReturn.setOnAction(event -> handleBtnReturn());
    }

    private void checkValidations(ActionEvent event) {
        try {
            if (event.getSource().equals(btnRegister)) {
                handleBtnRegister();
            }
            if (event.getSource().equals(btnCheckID)) {
                handleBtnCheckID();
            }
        } catch (IllegalArgumentException e) {
            generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    private void handleBtnRegister() {
        if(checkNum != 0) {
            throw new IllegalArgumentException("ID Not Checked!");
        }
        checkPasswordMismatch(password.getText(), confirmPassword.getText());
        checkPassword(password.getText());

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());

            map = new HashMap<>();
            String data = userId.getText() +","+ password.getText();
            map.put("register", data);
            oos.writeObject(map);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            int success = (int) ois.readObject();
            if(success > 0) {
                generateInformationAlert("You Are Registered! Moving To Login Page.").showAndWait();
                loadStage("fxml files/login-view.fxml", btnRegister.getScene());
            } else {
                throw new IOException("Register Fail!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleBtnCheckID() {
        checkUserId(userId.getText());
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());

            map = new HashMap<>();
            map.put("checkExistID", userId.getText());

            oos.writeObject(map);
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            checkNum = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(checkNum == 1) {
            checkNum = -1;
            throw new IllegalArgumentException("ID Already Exists!");
        } else if(checkNum == 0){
            generateInformationAlert("ID Available!").showAndWait();
        }
    }

    private void handleBtnReturn() {
        loadStage("fxml files/login-view.fxml", btnReturn.getScene());
    }
}
