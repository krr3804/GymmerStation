package com.gymmer.gymmerstation.util;

import com.gymmer.gymmerstation.Main;
import com.gymmer.gymmerstation.programOperation.PauseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Util {
    public static void loadStage(String fxml, Scene scene) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource(fxml));
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeStage(Button btn) {
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    public static String loadPauseWindow(Stage ownerWindow) {
        String pauseOption = "";
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("fxml files/pause-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Image ico = new Image("file:src/main/resources/com/gymmer/gymmerstation/images/dumbbell.png");
            stage.getIcons().add(ico);
            stage.setTitle("Gymmer Station");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            PauseController pauseController = loader.getController();
            Alerts.handleCloseOperationWindowAction(stage);
            stage.showAndWait();
            pauseOption = pauseController.returnOption();
            if (!pauseOption.equals("resume")) {
                ownerWindow.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pauseOption;
    }

}