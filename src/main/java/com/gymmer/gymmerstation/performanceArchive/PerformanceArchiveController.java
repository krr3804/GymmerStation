package com.gymmer.gymmerstation.performanceArchive;

import com.gymmer.gymmerstation.AppConfig;
import com.gymmer.gymmerstation.domain.OperationDataExercise;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;
import com.gymmer.gymmerstation.programOperation.ProgramOperationService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.Util.loadStage;

public class PerformanceArchiveController implements Initializable {
    private final ProgramOperationService programOperationService = AppConfig.programOperationService();
    private Program currentProgram = null;

    @FXML
    private Label programName;

    @FXML
    private Button btnReturn;

    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnReturn.setOnAction(event -> handleBtnReturnAction(event));
    }

    private void setTabPane() {
        for(OperationDataProgram dataProgram : programOperationService.getPerformanceDataList(currentProgram)) {
            StringBuilder title = new StringBuilder();
            title.append(dataProgram.getWeek()).append("-").append(dataProgram.getDivision());
            tabPane.getTabs().add(addTab(title.toString(),dataProgram));
        }
    }

    private Tab addTab(String title, OperationDataProgram dataProgram) {
        Tab tab = new Tab();
        tab.setText(title);
        tab.setContent(getTableView(dataProgram.getOdExerciseList()));
        return tab;
    }

    private TableView<OperationDataExercise> getTableView(List<OperationDataExercise> list) {
        TableView<OperationDataExercise> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableList(list));

        TableColumn<OperationDataExercise, String> nameColumn = new TableColumn<>("NAME");
        nameColumn.setPrefWidth(180);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<OperationDataExercise, Long> setColumn = new TableColumn<>("SET");
        setColumn.setPrefWidth(60);
        setColumn.setCellValueFactory(new PropertyValueFactory<>("currentSet"));

        TableColumn<OperationDataExercise, Long> repColumn = new TableColumn<>("REP");
        repColumn.setPrefWidth(60);
        repColumn.setCellValueFactory(new PropertyValueFactory<>("rep"));

        TableColumn<OperationDataExercise, Long> weightColumn = new TableColumn<>("WEIGHT");
        weightColumn.setPrefWidth(90);
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<OperationDataExercise, String> restColumn = new TableColumn<>("Rest");
        restColumn.setPrefWidth(90);
        restColumn.setCellValueFactory(new PropertyValueFactory<>("restTime"));

        TableColumn<OperationDataExercise, String> consumedTimeColumn = new TableColumn<>("TIME CONSUMED");
        consumedTimeColumn.setPrefWidth(120);
        consumedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("timeConsumed"));

        tableView.getColumns().addAll(nameColumn,setColumn,repColumn,weightColumn,restColumn,consumedTimeColumn);
        return tableView;
    }

    private void handleBtnReturnAction(ActionEvent event) {
        loadStage("fxml files/performance-archive-list-view.fxml", btnReturn.getScene());
    }

    public void initData(Program program) {
        currentProgram = program;
        programName.setText(currentProgram.getName());
        setTabPane();
    }
}
