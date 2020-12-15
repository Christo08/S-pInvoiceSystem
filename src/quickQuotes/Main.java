package quickQuotes;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import quickQuotes.controllers.MainController;
import quickQuotes.controllers.SettingsFileController;

import java.io.File;
import java.io.FileInputStream;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        SettingsFileController.initSettingsFileController(getParameters().getRaw().get(0));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));

        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setStage(primaryStage);
        Scene mainScene = new Scene(root, 900, 666);

        String CSSName = SettingsFileController.getTheme();
        String absoluteCSSPath1= SettingsFileController.getCSSPath()+"\\"+ CSSName;
        File f1 = new File(absoluteCSSPath1);
        String finalFilePath1="file:///" + f1.getAbsolutePath().replace("\\", "/");
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(finalFilePath1);
        primaryStage.setTitle("Quick Quotes");
        primaryStage.getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
