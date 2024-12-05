package com.gymmer.gymmerstation.performanceArchive;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.programOperation.ProgramOperationService;
import com.gymmer.gymmerstation.util.Alerts;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.gymmer.gymmerstation.util.CommonValidation.noIndexSelectedValidation;
import static com.gymmer.gymmerstation.util.Util.*;

public class PerformanceArchiveListController implements Initializable {
    private final ProgramOperationService programOperationService = AppConfig.programOperationService();
    private int selectedItemIndex = -1;
    private boolean status = false;
    private List<Program> programsInProgress;
    private List<Program> programsTerminated;

    @FXML
    private ListView<String> programListInProgress;

    @FXML
    private ListView<String> programListTerminated;

    @FXML
    private Button btnView, btnDelete, btnReturn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        programListInProgress.setItems(FXCollections.observableList(loadProgramList(false)));
        programListTerminated.setItems(FXCollections.observableList(loadProgramList(true)));
        formListDragDetectedAction(programListInProgress);
        formListDragDroppedAction(programListTerminated);
        handleListViewFocus();
        btnView.setOnAction(event -> handleButtonEvents(event));
        btnDelete.setOnAction(event -> handleButtonEvents(event));
        btnReturn.setOnAction(event -> handleBtnReturnAction(event));
    }

    private void handleListViewFocus() {
        programListInProgress.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(btnView.isFocused() || btnDelete.isFocused()) {
                return;
            }
            if(!newVal) {
                programListInProgress.getSelectionModel().clearSelection();
            }
        });
        programListTerminated.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (btnView.isFocused() || btnDelete.isFocused()) {
                return;
            }
            if (!newVal) {
                programListTerminated.getSelectionModel().clearSelection();
            }
        });
    }

    private void handleButtonEvents(ActionEvent event) {
        try {
            if(event.getSource().equals(btnView)) {
                handleBtnViewAction(event);
            }
            if (event.getSource().equals(btnDelete)) {
                handleBtnDeleteAction(event);
            }
        } catch (IllegalArgumentException e) {
            Alerts.generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    private void formListDragDetectedAction(ListView<String> list) {
        list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> listCell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                listCell.setOnDragDetected((MouseEvent event) -> {
                    selectedItemIndex = programListInProgress.getSelectionModel().getSelectedIndex();
                    Dragboard db = listCell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(listCell.getItem());
                    db.setContent(content);
                    if(programListTerminated.getItems().isEmpty()) {
                        programListTerminated.getItems().add("");
                    }
                });

                listCell.setOnDragDone((DragEvent event) -> {
                    if(programListTerminated.getItems().get(0).equals("")) {
                        programListTerminated.setItems(FXCollections.observableList(loadProgramList(true)));
                    }
                    selectedItemIndex = -1;
                });

                return listCell;
            }
        });
    }
    private void formListDragDroppedAction(ListView<String> list) {
        list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> listCell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };

                listCell.setOnDragEntered(event -> listCell.setStyle( "-fx-background-color: grey;" ));

                listCell.setOnDragExited(event -> listCell.setStyle(""));

                listCell.setOnDragOver( ( DragEvent event ) ->
                {
                    Dragboard db = event.getDragboard();
                    if ( db.hasString() )
                    {
                        event.acceptTransferModes( TransferMode.MOVE );
                    }
                } );

                listCell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    if (db.hasString()) {
                        terminateProgram(event);
                        event.setDropCompleted(true);
                    } else {
                        event.setDropCompleted(false);
                    }
                    selectedItemIndex = -1;
                });
                return listCell;
            }
        });
    }

    private List<String> loadProgramList(boolean terminationStatus) {
        if(!terminationStatus) {
            programsInProgress = programOperationService.getProgramsInArchive(false);
            return programsInProgress.stream().map(program -> program.getName() + "(" + programOperationService.getProgress(program.getId()) + "/"
                    + (program.getLength() * program.getDivisionQty()) + ")").collect(Collectors.toList());
        } else {
            programsTerminated = programOperationService.getProgramsInArchive(true);
            return programsTerminated.stream().map(program -> program.getName() + "(complete)").collect(Collectors.toList());
        }
    }

    private void terminateProgram(DragEvent event) {
        Program selectedProgram = programsInProgress.get(selectedItemIndex);
        Optional<ButtonType> result = Alerts.generateTerminateProgramAlert(selectedProgram.getName()).showAndWait();
        if(result.get() == ButtonType.YES) {
            programOperationService.terminateProgram(selectedProgram.getId());
            programListInProgress.setItems(FXCollections.observableList(loadProgramList(false)));
            programListTerminated.setItems(FXCollections.observableList(loadProgramList(true)));
        }
    }

    private void handleBtnViewAction(ActionEvent event) {
        checkSelectedList();
        noIndexSelectedValidation(selectedItemIndex);
        Program selectedProgram = null;
        if(status) {
            selectedProgram = programsTerminated.get(selectedItemIndex);
        } else {
            selectedProgram = programsInProgress.get(selectedItemIndex);
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("fxml files/performance-archive-view.fxml"));
            Parent root = loader.load();
            PerformanceArchiveController performanceArchiveController = loader.getController();
            performanceArchiveController.initData(selectedProgram);
            Stage stage = (Stage) btnView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkSelectedList() {
        if(programListInProgress.getSelectionModel().getSelectedItem() != null && programListTerminated.getSelectionModel().getSelectedItem() == null) {
            selectedItemIndex = programListInProgress.getSelectionModel().getSelectedIndex();
            status = false;
        }
        if(programListInProgress.getSelectionModel().getSelectedItem() == null && programListTerminated.getSelectionModel().getSelectedItem() != null) {
            selectedItemIndex = programListTerminated.getSelectionModel().getSelectedIndex();
            status = true;
        }
    }

    private void handleBtnDeleteAction(ActionEvent event) {
        checkSelectedList();
        noIndexSelectedValidation(selectedItemIndex);
        Long selectedProgramId = null;
        if(status) {
            selectedProgramId = programsTerminated.get(selectedItemIndex).getId();
        } else {
            selectedProgramId = programsInProgress.get(selectedItemIndex).getId();
        }
        Alert alert = Alerts.generateDeleteDataAlert("");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            programOperationService.deletePerformanceData(selectedProgramId);
            programListInProgress.setItems(FXCollections.observableList(loadProgramList(false)));
            programListTerminated.setItems(FXCollections.observableList(loadProgramList(true)));
            Alerts.generateInformationAlert("Data Deleted!").showAndWait();
        } else {
            alert.close();
            programListInProgress.getSelectionModel().clearSelection();
            programListTerminated.getSelectionModel().clearSelection();
        }
        selectedItemIndex = -1;
    }

    private void handleBtnReturnAction(ActionEvent event) {
        loadStage("fxml files/main-view.fxml", btnReturn.getScene());
    }
}
