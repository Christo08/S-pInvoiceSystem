package quickQuotes;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import quickQuotes.controllers.MainController;

import java.io.File;
import java.io.FileInputStream;

public class Main extends Application {
    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));

        Parent root = loader.load();
        MainController mainController = (MainController)loader.getController();
        mainController.setStage(primaryStage);
        Scene mainScene = new Scene(root, 790, 666);

        String CSSName = mainController.getTheme();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(CSSName);
        primaryStage.setTitle("Quick Quotes");
        primaryStage.getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
