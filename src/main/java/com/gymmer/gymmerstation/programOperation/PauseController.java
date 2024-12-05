package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.Util.*;
import static com.gymmer.gymmerstation.util.Alerts.generateSaveAlert;

public class PauseController implements Initializable {
    private final ProgramOperationService programOperationService = AppConfig.programOperationService();
    private String pauseOption;

    @FXML
    private Button btnResume, btnSaveAndExit, btnExit;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnResume.setOnAction(event -> handleBtnResumeAction(event));
        btnSaveAndExit.setOnAction(event -> handleBtnSaveAndExitAction(event));
        btnExit.setOnAction(event -> handleBtnExitAction(event));
    }

    private void handleBtnResumeAction(ActionEvent event) {
        pauseOption = "resume";
        closeStage(btnResume);
    }

    private void handleBtnSaveAndExitAction(ActionEvent event) {
        Optional<ButtonType> result = Alerts.generateExitProgramInProgressAlert().showAndWait();
        if(result.get() == ButtonType.OK) {
            pauseOption = "saveAndExit";
            closeStage(btnSaveAndExit);
        }
    }

    private void handleBtnExitAction(ActionEvent event) {
        Optional<ButtonType> resultExit = Alerts.generateExitProgramInProgressAlert().showAndWait();
        if(resultExit.get() == ButtonType.OK) {
            Alert alert = generateSaveAlert();
            alert.getButtonTypes().remove(ButtonType.CANCEL);
            Optional<ButtonType> resultSave = alert.showAndWait();
            if (resultSave.get() == ButtonType.YES) {
                pauseOption = "saveAndExit";
            } else {
                pauseOption = "exit";
            }
            closeStage(btnExit);
        }
    }

    public String returnOption() {
        return pauseOption;
    }
}
