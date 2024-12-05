package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.OperationDataExercise;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.home.User;
import com.gymmer.gymmerstation.programManagement.ProgramService;
import com.gymmer.gymmerstation.util.Alerts;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
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
    private String pauseOption;
    private Stage operationStage;

    @FXML
    private Label programNameInfo, purposeInfo, divisionInfo;

    @FXML
    private Button btnStart, btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        purposeInfo.setWrapText(true);
        setOperationStage();
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
        if (!odeList.isEmpty()) {
            programOperationService.savePerformanceData(new OperationDataProgram(currentProgram,week,division,odeList));
        }
        checkProgramCompletion();
        currentStage.show();
        Platform.runLater(() -> {
            btnStart.setText("Completed");
            btnStart.setDisable(true);
        });
    }

    private void checkProgramCompletion() {
        if(programOperationService.getProgress(currentProgram.getId()) == currentProgram.getLength() * currentProgram.getDivisionQty()) {
            programOperationService.terminateProgram(currentProgram.getId());
            Alerts.generateCompletionMessage(currentProgram.getName()).showAndWait();
        }
    }

    private void setOperationStage() {
        operationStage = new Stage();
        Image ico = new Image("file:src/main/resources/com/gymmer/gymmerstation/images/dumbbell.png");
        operationStage.getIcons().add(ico);
        operationStage.setTitle("Gymmer Station");
    }

    private List<OperationDataExercise> operateProgram() {
        List<OperationDataExercise> odeList = new ArrayList<>();
        outerLoop:
        for(Exercise exercise : currentProgram.getExerciseByDivision(division)) {
            String weight = null;
            Long reps = null;
            String timeConsumed = null;
            for(long set = 1; set <= exercise.getSet(); set++) {
                String[] operationResult = loadOperationStage(exercise, set, weight);
                weight = operationResult[0];
                reps = Long.parseLong(operationResult[1]);
                timeConsumed = operationResult[2];
                if (pauseOption.equals("saveAndExit")) {
                    break outerLoop;
                }
                if (pauseOption.equals("exit")) {
                    throw new IllegalArgumentException();
                }
                odeList.add(new OperationDataExercise(exercise.getName(), set, reps, weight, exercise.getRestTime(), timeConsumed));
                loadRestTimeStage(exercise);
                if (pauseOption.equals("saveAndExit")) {
                    break outerLoop;
                }
                if (pauseOption.equals("exit")) {
                    throw new IllegalArgumentException();
                }
                pauseOption = "";
            }
        }
        return odeList;
    }

    private String[] loadOperationStage(Exercise exercise, Long currentSet, String weight) {
        String[] operationResult = null;
        try {
            FXMLLoader operationLoader = new FXMLLoader(Main.class.getResource("fxml files/program-operation-view.fxml"));
            Parent root = operationLoader.load();
            ProgramOperationController programOperationController = operationLoader.getController();
            programOperationController.initData(exercise,currentSet,weight);
            operationStage.setScene(new Scene(root));
            operationStage.setOnCloseRequest(event -> {
                event.consume();
                programOperationController.handleWatchOnCloseRequest();
                Alert alert = Alerts.generateExitProgramInProgressAlert();
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Socket socket = User.socket;
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
                } else {
                    alert.close();
                    programOperationController.handleWatchOnCloseRequest();
                }
            });
            operationStage.showAndWait();

            pauseOption = programOperationController.returnOption();
            operationResult = programOperationController.getOperationResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return operationResult;
    }

    private void loadRestTimeStage(Exercise exercise) {
        try {
            FXMLLoader restLoader = new FXMLLoader(Main.class.getResource("fxml files/rest-time-view.fxml"));
            Parent root = restLoader.load();
            RestTimeController restTimeController = restLoader.getController();
            restTimeController.initData(exercise.getRestTime().substring(0,2),exercise.getRestTime().substring(3,5));
            operationStage.setScene(new Scene(root));
            operationStage.setOnCloseRequest(event -> {
                event.consume();
                restTimeController.handleWatchOnCloseRequest();
                Alert alert = Alerts.generateExitProgramInProgressAlert();
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Socket socket = User.socket;
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
        loadStage("fxml files/main-view.fxml",btnStart.getScene());
    }

    private void handleBtnExitAction(ActionEvent event) {
        loadStage("fxml files/load-program-view.fxml",btnExit.getScene());
    }

    public void initProgramData(Program program) {
        currentProgram = program;
        programNameInfo.setText(currentProgram.getName());
        purposeInfo.setText(currentProgram.getPurpose());
        week = programOperationService.getCurrentWeek(currentProgram);
        division = programOperationService.getCurrentDivision(currentProgram);
        divisionInfo.setText("Week " + week + " - " + division);
    }
}
