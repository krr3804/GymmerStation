package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Exercise;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.Alerts.generateErrorAlert;
import static com.gymmer.gymmerstation.util.Alerts.handleCloseWindowAction;
import static com.gymmer.gymmerstation.util.CommonValidation.inputMismatchValidationNumber;
import static com.gymmer.gymmerstation.util.Util.*;

public class ProgramOperationController implements Initializable {
    private long sec;
    private boolean stop;
    private String pauseOption = "resume";
    private String reps;

    @FXML
    private Label exerciseInfo, setInfo, weightInfo, minute, second;

    @FXML
    private Button btnStart, btnDone;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if(weightInfo.getText().isBlank()) {
                loadPopup("weight");
            }
        });
        btnStart.setOnAction(event -> handleBtnStartAction(event));
        btnDone.setOnAction(event -> handleBtnDoneAction(event));
    }

    public void loadPopup(String field) {
        Stage primaryStage = (Stage) btnStart.getScene().getWindow();
        Stage popup = new Stage();
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initOwner(primaryStage);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml files/input-popup-view.fxml"));
            Parent root = loader.load();
            InputPopupController inputPopupController = loader.getController();
            inputPopupController.initPopupData(field);
            popup.setScene(new Scene(root));
            popup.setTitle("Gymmer Station");
            popup.getIcons().add(new Image("file:src/main/resources/com/gymmer/gymmerstation/images/dumbbell.png"));
            popup.setResizable(false);
            handleCloseWindowAction(popup);
            popup.showAndWait();
            if(field.equals("weight")) {
                weightInfo.setText(inputPopupController.getInput());
            } else if (field.equals("reps")) {
                reps = inputPopupController.getInput();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        stop = false;
        Thread thread = new Thread(() -> {
            while (!stop && sec < 3600) {
                long m = sec % 3600 / 60;
                long s = sec % 60;

                Platform.runLater(() -> {
                    minute.setText(String.format("%02d",m));
                    second.setText(String.format("%02d",s));
                });
                try {
                    Thread.sleep(1000);
                    sec++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void handleWatchOnCloseRequest() {
        if(!stop) {
            stop = true;
        } else {
            start();
        }
    }

    private void handleBtnStartAction(ActionEvent event) {
        if(btnStart.getText().equals("Start")) {
            start();
            btnStart.setText("Pause");
            return;
        }
        handlePauseAction();
    }

    private void handlePauseAction() {
        stop = true;
        pauseOption = loadPauseWindow((Stage) btnStart.getScene().getWindow());
        start();
    }

    public String returnOption() {
        return pauseOption;
    }

    private void handleBtnDoneAction(ActionEvent event) {
        stop = true;
        Platform.runLater(() -> {
            loadPopup("reps");
            Stage stage = (Stage)btnDone.getScene().getWindow();
            stage.hide();
        });
    }

    public void initData(Exercise exercise, Long currentSet, String weight) {
        exerciseInfo.setText(exercise.getName());
        setInfo.setText(currentSet.toString());
        if(exercise.getWeightType().equals("Body weight")) {
            weightInfo.setText("Body weight");
        }
        if(exercise.getWeightType().equals("Fixed") && currentSet > 1) {
            weightInfo.setText(weight);
        }
    }

    public String[] getOperationResult() {
        String[] operationResult = new String[3];
        operationResult[0] = weightInfo.getText();
        operationResult[1] = reps;
        operationResult[2] = minute.getText()+":"+second.getText();
        return operationResult;
    }
}
