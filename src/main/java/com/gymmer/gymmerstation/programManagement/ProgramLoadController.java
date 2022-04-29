package com.gymmer.gymmerstation.programManagement;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.programOperation.ProgramInformationController;
import com.gymmer.gymmerstation.programOperation.ProgramOperationService;
import com.gymmer.gymmerstation.util.CommonValidation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.CommonValidation.noIndexSelectedValidation;
import static com.gymmer.gymmerstation.util.Util.*;
import static javafx.collections.FXCollections.observableList;

public class ProgramLoadController implements Initializable {
    private final ProgramService programService = AppConfig.programService();
    private final ProgramOperationService programOperationService = AppConfig.programOperationService();
    private static int index = -1;

    @FXML
    ListView<String> programList;

    @FXML
    Button btnStart;

    @FXML
    Button btnEdit;

    @FXML
    Button btnDelete;

    @FXML
    Button btnReturn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        programList.setItems(observableList(programService.showProgramList()));
        btnReturn.setOnAction(event -> loadStage("main-view.fxml",btnReturn.getScene()));
        btnStart.setOnAction(event -> handleButtonEvents(event));
        btnEdit.setOnAction(event -> handleButtonEvents(event));
        btnDelete.setOnAction(event -> handleButtonEvents(event));
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
                generateInformationAlert("Program Deleted!").showAndWait();
            }
        } catch (IllegalArgumentException e) {
            generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    private void handleBtnStart(ActionEvent event) {
        index = programList.getSelectionModel().getSelectedIndex();
        noIndexSelectedValidation(index);
        try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("program-information-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                ProgramInformationController programInformationController = loader.getController();
                programInformationController.initProgramData(index);
                stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        index = -1;
    }

    private void handleBtnDelete(ActionEvent event) {
        index = programList.getSelectionModel().getSelectedIndex();
        noIndexSelectedValidation(index);
        Program program = programService.getProgramById(index);
        Alert alert = generateDeleteDataAlert(program.getName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            programOperationService.terminateProgram(program);
            programService.deleteProgram(program.getId());
            programList.setItems(observableList(programService.showProgramList()));
            index = -1;
        } else {
            alert.close();
        }
    }

    private void handleBtnEdit(ActionEvent event) {
        index = programList.getSelectionModel().getSelectedIndex();
        noIndexSelectedValidation(index);
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("edit-program-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnEdit.getScene().getWindow();
            stage.setScene(new Scene(root));
            ProgramEditController programEditController = loader.getController();
            programEditController.initEditData(index);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        index=-1;
    }
}
