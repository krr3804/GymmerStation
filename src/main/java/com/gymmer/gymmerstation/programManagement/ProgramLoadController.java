package com.gymmer.gymmerstation.programManagement;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.programOperation.ProgramInformationController;
import com.gymmer.gymmerstation.programOperation.ProgramOperationService;
import com.gymmer.gymmerstation.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.gymmer.gymmerstation.util.Alerts.generateErrorAlert;
import static com.gymmer.gymmerstation.util.Alerts.generateInformationAlert;
import static com.gymmer.gymmerstation.util.CommonValidation.noIndexSelectedValidation;
import static com.gymmer.gymmerstation.util.Util.*;
import static javafx.collections.FXCollections.observableList;

public class ProgramLoadController implements Initializable {
    private final ProgramService programService = AppConfig.programService();
    private final ProgramOperationService programOperationService = AppConfig.programOperationService();
    private static int index = -1;
    private List<Program> programs = new ArrayList<>();
    private Program selectedProgram;

    @FXML
    private ListView<String> programList;

    @FXML
    private Label progressTxt;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button btnStart, btnEdit, btnDelete, btnReturn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        programList.setItems(FXCollections.observableList(loadProgramList()));
        programList.setOnMouseClicked(event -> handleProgressBar(event));
        btnReturn.setOnAction(event -> loadStage("fxml files/main-view.fxml",btnReturn.getScene()));
        btnStart.setOnAction(event -> handleButtonEvents(event));
        btnEdit.setOnAction(event -> handleButtonEvents(event));
        btnDelete.setOnAction(event -> handleButtonEvents(event));
    }

    private List<String> loadProgramList() {
        programs = programService.getProgramList();
        return programs.stream().map(Program::getName).collect(Collectors.toList());
    }

    private void handleProgressBar (MouseEvent event) {
        if (!programList.getSelectionModel().isEmpty()) {
            index = programList.getSelectionModel().getSelectedIndex();
            noIndexSelectedValidation(index);
            selectedProgram = programs.get(index);
            int workDone = programOperationService.getProgress(selectedProgram.getId());
            long totalWork = selectedProgram.getLength() * selectedProgram.getDivisionQty();
            progressTxt.setText(workDone + "/" + totalWork);
            double progress = (double)workDone / totalWork;
            progressBar.setProgress(progress);
        } else {
            progressTxt.setText("");
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }
    }

    private void handleButtonEvents(ActionEvent event) {
        try {
            if(event.getSource().equals(btnStart)) {
                handleBtnStart(event);
            }
            if (event.getSource().equals(btnEdit)) {
                handleBtnEdit(event);
            }
            if (event.getSource().equals(btnDelete)) {
                handleBtnDelete(event);
            }
        } catch (IllegalArgumentException e) {
            generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    private void handleBtnStart(ActionEvent event) {
        index = programList.getSelectionModel().getSelectedIndex();
        noIndexSelectedValidation(index);
        selectedProgram = programs.get(index);
        try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("fxml files/program-information-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                ProgramInformationController programInformationController = loader.getController();
                programInformationController.initProgramData(selectedProgram);
                stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        index = -1;
    }

    private void handleBtnDelete(ActionEvent event) {
        index = programList.getSelectionModel().getSelectedIndex();
        noIndexSelectedValidation(index);
        selectedProgram = programs.get(index);
        Alert alert = Alerts.generateDeleteDataAlert(selectedProgram.getName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if(programOperationService.getProgress(selectedProgram.getId()) > 0) {
                programOperationService.terminateProgram(selectedProgram.getId());
            }
            programService.deleteProgram(selectedProgram.getId());
            programList.setItems(observableList(loadProgramList()));
            programList.getSelectionModel().clearSelection();
            index = -1;
            generateInformationAlert("Program Deleted!").showAndWait();
        } else {
            alert.close();
        }
    }

    private void handleBtnEdit(ActionEvent event) {
        index = programList.getSelectionModel().getSelectedIndex();
        noIndexSelectedValidation(index);
        selectedProgram = programs.get(index);
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("fxml files/edit-program-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnEdit.getScene().getWindow();
            stage.setScene(new Scene(root));
            ProgramEditController programEditController = loader.getController();
            programEditController.initEditData(selectedProgram);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        index=-1;
    }
}
