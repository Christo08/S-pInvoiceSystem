package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.controllers.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        MainController mainController = (MainController)loader.getController();
        mainController.setStage(primaryStage);
        primaryStage.setTitle("Invoice");
        primaryStage.getIcons().add(new Image("sample/resource/Logo.PNG"));
        primaryStage.setScene(new Scene(root, 780, 666));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}