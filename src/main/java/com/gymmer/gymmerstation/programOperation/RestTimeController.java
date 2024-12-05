package com.gymmer.gymmerstation.programOperation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.gymmer.gymmerstation.util.Util.*;

public class RestTimeController implements Initializable {
    private boolean stop;
    private long sec;
    private String pauseOption = "resume";

    @FXML
    private Label minute, second;

    @FXML
    private Button btnPause;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPause.setOnAction(event -> handleBtnPauseAction(event));
        start();
    }

    private void start() {
        stop = false;
        Thread thread = new Thread(() -> {
            while (!stop && sec >= 0) {
                if(sec == 0) {
                    Platform.runLater(() -> {
                        Stage stage = (Stage)btnPause.getScene().getWindow();
                        stage.hide();
                    });
                }

                long m = sec % 3600 / 60;
                long s = sec % 60;

                Platform.runLater(() -> {
                    minute.setText(String.format("%02d",m));
                    second.setText(String.format("%02d",s));
                });

                try {
                    Thread.sleep(1000);
                    sec--;
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

    private void handleBtnPauseAction(ActionEvent event) {
        stop = true;
        pauseOption = loadPauseWindow((Stage) btnPause.getScene().getWindow());
        start();
    }

    public String returnPauseOption() {
        return pauseOption;
    }

    public void initData(String minInfo, String secInfo) {
        minute.setText(minInfo);
        second.setText(secInfo);
        sec = Long.parseLong(minInfo)*60 + Long.parseLong(secInfo);
    }
}
