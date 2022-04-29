package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.OperationDataExercise;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.programManagement.ProgramService;
import com.gymmer.gymmerstation.util.Util;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.Util.*;

public class ProgramInformationController implements Initializable {
    private final ProgramService programService = AppConfig.programService();
    private final ProgramOperationService programOperationService = AppConfig.programOperationService();
    private Program currentProgram = null;
    private Long week;
    private Long division;
    private String timeConsumed;
    private String pauseOption;
    private Stage operationStage = new Stage();

    @FXML
    private Label programNameInfo,  purposeInfo, lengthInfo, divisionInfo;

    @FXML
    private Button btnStart, btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnStart.setOnAction(event -> handleBtnStartAction(event));
        btnExit.setOnAction(event -> handleBtnExitAction(event));
    }

    private void handleBtnStartAction(ActionEvent event) {
        Stage currentStage = (Stage) btnStart.getScene().getWindow();
        currentStage.hide();
        List<OperationDataExercise> odeList;
        try {
            odeList = operateProgram();
        } catch (Exception e) {
            exitToMain();
            return;
        }
        programOperationService.saveProgramData(new OperationDataProgram(currentProgram,week,division,odeList));
        currentStage.show();
        Platform.runLater(() -> {
            btnStart.setText("Completed");
            btnStart.setDisable(true);
        });
    }

    private List<OperationDataExercise> operateProgram() {
        List<OperationDataExercise> odeList = new ArrayList<>();
        outerLoop:
        for(Exercise exercise : currentProgram.getExerciseByDivision(division)) {
            for(long set = 1; set <= exercise.getSet(); set++) {
                loadOperationStage(exercise,set);
                if (pauseOption.equals("saveAndExit")) {
                    break outerLoop;
                }
                if (pauseOption.equals("exit")) {
                    throw new IllegalArgumentException();
                }
                odeList.add(new OperationDataExercise(exercise.getName(), set, exercise.getRep(), exercise.getWeight(), exercise.getRestTime(), timeConsumed));
                loadRestTimeStage(exercise);
                if (pauseOption.equals("saveAndExit")) {
                    break outerLoop;
                }
                if (pauseOption.equals("exit")) {
                    throw new IllegalArgumentException();
                }
                pauseOption = "";
                timeConsumed = "";
            }
        }
        return odeList;
    }

    private void loadOperationStage(Exercise exercise, Long currentSet) {
        try {
            FXMLLoader operationLoader = new FXMLLoader(Main.class.getResource("program-operation-view.fxml"));
            Parent root = operationLoader.load();
            ProgramOperationController programOperationController = operationLoader.getController();
            programOperationController.initData(exercise,currentSet);
            operationStage.setScene(new Scene(root));
            operationStage.setOnCloseRequest(event -> {
                event.consume();
                programOperationController.handleWatchOnCloseRequest();
                Alert alert = generateExitProgramAlert();
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    System.exit(0);
                } else {
                    alert.close();
                    programOperationController.handleWatchOnCloseRequest();
                }
            });
            operationStage.showAndWait();
            pauseOption = programOperationController.returnOption();
            timeConsumed = programOperationController.getTimeConsumed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRestTimeStage(Exercise exercise) {
        try {
            FXMLLoader restLoader = new FXMLLoader(Main.class.getResource("rest-time-view.fxml"));
            Parent root = restLoader.load();
            RestTimeController restTimeController = restLoader.getController();
            restTimeController.initData(exercise.getRestTime().substring(0,2),exercise.getRestTime().substring(3,5));
            operationStage.setScene(new Scene(root));
            operationStage.setOnCloseRequest(event -> {
                event.consume();
                restTimeController.handleWatchOnCloseRequest();
                Alert alert = generateExitProgramAlert();
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    System.exit(0);
                } else {
                    alert.close();
                    restTimeController.handleWatchOnCloseRequest();
                }
            });
            operationStage.showAndWait();
            pauseOption = restTimeController.returnPauseOption();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitToMain() {
        loadStage("main-view.fxml",btnStart.getScene());
    }

    private void handleBtnExitAction(ActionEvent event) {
        loadStage("load-program-view.fxml",btnExit.getScene());
    }

    public void initProgramData(int index) {
        currentProgram = programService.getProgramById(index);
        programNameInfo.setText(currentProgram.getName());
        purposeInfo.setText(currentProgram.getPurpose());
        lengthInfo.setText(currentProgram.getLength().toString());
        week = programOperationService.getCurrentWeek(currentProgram);
        division = programOperationService.getCurrentDivision(currentProgram);
        divisionInfo.setText("Week " + week + " - " + division);
    }
}
