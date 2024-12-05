package com.gymmer.gymmerstation.programOperation;

import com.gymmer.gymmerstation.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.Alerts.generateErrorAlert;
import static com.gymmer.gymmerstation.util.CommonValidation.inputMismatchValidationNumber;

public class InputPopupController implements Initializable {
    private String field;
    private String weightInput;
    private String repsInput;

    @FXML
    private Label popupLabel;

    @FXML
    private TextField input;

    @FXML
    private Button btnSubmit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popupLabel.setWrapText(true);
        btnSubmit.setOnAction(event -> handleBtnSubmitAction(field));
    }

    private void handleBtnSubmitAction(String field) {
        try {
            if(input.getText().isBlank()) {
                throw new IllegalArgumentException("Input Is Blank!");
            }
            inputMismatchValidationNumber(input.getText(),field);
            if(field.equals("weight")) {
                weightInput = input.getText();
            } else if (field.equals("reps")) {
                repsInput = input.getText();
            }
            Util.closeStage(btnSubmit);
        } catch (IllegalArgumentException e) {
            generateErrorAlert(e.getMessage()).showAndWait();
        }
    }

    public void initPopupData(String field) {
        this.field = field;
        if(field.equals("weight")) {
            popupLabel.setText("SET WEIGHT :");
        } else if (field.equals("reps")) {
            popupLabel.setText("How many have you lifted?");
        }
    }

    public String getInput() {
        if(field.equals("weight")) {
            return weightInput;
        }
        return repsInput;
    }
}
