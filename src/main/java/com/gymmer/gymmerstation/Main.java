package com.gymmer.gymmerstation;

import com.gymmer.gymmerstation.programManagement.ProgramService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static com.gymmer.gymmerstation.util.Alerts.handleCloseWindowAction;

public class Main extends javafx.application.Application {
    private static ProgramService programService = AppConfig.programService();

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("fxml files/main-view.fxml"));
            Scene scene = new Scene(root);
            Image ico = new Image("file:src/main/resources/com/gymmer/gymmerstation/images/dumbbell.png");
            primaryStage.getIcons().add(ico);
            primaryStage.setTitle("Gymmer Station");
            handleCloseWindowAction(primaryStage);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}