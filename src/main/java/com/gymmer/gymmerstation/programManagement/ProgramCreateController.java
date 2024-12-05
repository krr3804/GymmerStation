package com.gymmer.gymmerstation.programManagement;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.exerciseManagement.ExerciseController;
import com.gymmer.gymmerstation.programManagement.validations.InputValidation;
import com.gymmer.gymmerstation.util.Alerts;
import com.gymmer.gymmerstation.util.CommonValidation;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static com.gymmer.gymmerstation.programManagement.validations.DivisionValidation.*;
import static com.gymmer.gymmerstation.util.CommonValidation.noItemSelectedValidation;
import static com.gymmer.gymmerstation.util.Util.loadStage;

public class ProgramCreateController implements Initializable {

    private final ProgramService programService = AppConfig.programService();
    private Program program = new Program(new ArrayList<>());
    private List<Integer> divisionList = new ArrayList<>();

    @FXML
    private TextField inpName;

    @FXML
    private TextArea inpPurpose;

    @FXML
    private TextField inpLength;

    @FXML
    private ListView<Integer> divisionListView;

    @FXML
    private Button btnAddDivision, btnRemoveDivision, btnSave, btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inpPurpose.setWrapText(true);
        divisionListView.setOnMouseClicked(event -> handleListDoubleClickEvent(event));
        divisionListView.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(btnRemoveDivision.isFocused()) {
                return;
            }
            if(!newVal) {
                divisionListView.getSelectionModel().clearSelection();
            }
        });
        inpLength.setOnKeyTyped(event -> checkInputEventValidation(event));
        btnAddDivision.setOnAction(event -> checkButtonEventValidation(event));
        btnRemoveDivision.setOnAction(event -> checkButtonEventValidation(event));
        btnSave.setOnAction(event -> checkButtonEventValidation(event));
        btnExit.setOnAction(event -> checkButtonEventValidation(event));
    }

    private void checkInputEventValidation(KeyEvent event) {
        try {
            CommonValidation.inputMismatchValidationNumber(inpLength.getText(), "Length");
        } catch (IllegalArgumentException e) {
            inpLength.clear();
            Alerts.generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    private void checkButtonEventValidation(ActionEvent event) {
        try {
            if (event.getSource().equals(btnSave)) {
                handleBtnSaveAction(event);
                Alerts.generateInformationAlert("Program Saved!").showAndWait();
            }
            if (event.getSource().equals(btnAddDivision)) {
                handleBtnAddDivisionEvent(event);
            }
            if (event.getSource().equals(btnRemoveDivision)) {
                handleBtnRemoveDivisionEvent(event);
            }
            if (event.getSource().equals(btnExit)) {
                handleBtnExitAction(event);
            }
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals("Data Unsaved!")) {
                Alert alert = Alerts.generateSaveAlert();
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.YES) {
                    btnSave.fire();
                } else if(result.get() == ButtonType.NO) {
                    loadStage("fxml files/main-view.fxml", btnExit.getScene());
                } else {
                    alert.close();
                }
            } else {
                Alerts.generateErrorAlert(e.getMessage()).showAndWait();
            }
        }
    }

    private void handleBtnExitAction(ActionEvent event) {
        if(checkDataUnsaved()) {
            throw new IllegalArgumentException("Data Unsaved!");
        }
        loadStage("fxml files/main-view.fxml", btnExit.getScene());
    }

    private boolean checkDataUnsaved() {
        return !inpName.getText().equals("") || !inpPurpose.getText().equals("") ||
                !inpLength.getText().equals("") || !divisionList.isEmpty();
    }

    private void handleBtnSaveAction(ActionEvent event) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("Name", inpName.getText());
        map.put("Purpose", inpPurpose.getText());
        map.put("Length", inpLength.getText());
        InputValidation.inputBlankValidation(map);
        noDivisionValidation(divisionList);
        noExerciseValidation(program,divisionList);
        program.setName(inpName.getText());
        program.setPurpose(inpPurpose.getText());
        program.setLength(Long.parseLong(inpLength.getText()));
        program.setDivisionQty(program.countDivision());
        programService.addProgram(program);
        loadStage("fxml files/main-view.fxml", btnExit.getScene());
    }

    private void handleBtnAddDivisionEvent(ActionEvent event) {
        noExerciseValidation(program,divisionList);
        divisionList.add(divisionList.size() + 1);
        divisionListView.setItems(getDivision());
    }

    private void handleBtnRemoveDivisionEvent(ActionEvent event) {
        noItemSelectedValidation(divisionListView.getSelectionModel().getSelectedItem());
        Long selectedDivision = divisionListView.getSelectionModel().getSelectedItem().longValue();
        if(emptyDivisionValidation(program,selectedDivision)) {
            removeDivision(selectedDivision);
            return;
        }
        Alert alert = Alerts.generateDeleteDivisionAlert(selectedDivision);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            program.removeExerciseInDivision(selectedDivision);
            removeDivision(selectedDivision);
        } else {
            alert.close();
        }
    }

    private void removeDivision(Long selectedDivision) {
        int index = divisionListView.getSelectionModel().getSelectedIndex();
        divisionList.remove(index);
        for (int i = index; i < divisionList.size(); i++) {
            divisionList.set(i, divisionList.get(i) - 1);
        }
        divisionListView.setItems(getDivision());
    }

    private ObservableList<Integer> getDivision() {
        return FXCollections.observableList(divisionList);
    }

    private void handleListDoubleClickEvent(MouseEvent event) {
        try {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                noItemSelectedValidation(divisionListView.getSelectionModel().getSelectedItem());
                Long selectedDivision = divisionListView.getSelectionModel().getSelectedItem().longValue();
                loadExerciseWindow(program, selectedDivision, event);
            }
        } catch (IllegalArgumentException e) {
            Alerts.generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    private void loadExerciseWindow(Program program, Long division, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("fxml files/exercise-form-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Image ico = new Image("file:src/main/resources/com/gymmer/gymmerstation/images/dumbbell.png");
            stage.getIcons().add(ico);
            stage.setTitle("Gymmer Station");
            stage.setScene(new Scene(root));
            ExerciseController exerciseController = loader.getController();
            exerciseController.initData(program, division);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            Alerts.handleCloseWindowAction(stage);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
