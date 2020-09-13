package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import sample.dataReader.Item;
import javafx.application.Platform;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();
    private Stage primaryStage;
    private String activeFileName = null;

    @FXML
    private SplitPane invoice;

    @FXML
    private SplitPane tad1;

    @FXML
    private InvoiceController invoiceController;

    @FXML
    private Tab1Controller tab1Controller;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    private MenuItem menuItemSave;

    @FXML
    private MenuItem menuItemSaveAs;

    @FXML
    private MenuItem menuItemQuit;

    public void setStage(Stage stage) throws Exception{
        primaryStage = stage;
        // set fileChooser extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        menuItemOpen.setOnAction((event) -> Open());
        menuItemSave.setOnAction((event) -> Save());
        menuItemSaveAs.setOnAction((event) -> SaveAs());
        menuItemQuit.setOnAction((event) -> Quit());
    }

    private void Open(){
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            activeFileName = file.getName();
            System.out.println("Opened file "+activeFileName);

            try {
                invoiceController.clearInvoice();

                XSSFWorkbook  workbook = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;
                XSSFCell cell;

                int rows = sheet.getPhysicalNumberOfRows();

                for(int r = 1; r < rows; r++) {
                    row = sheet.getRow(r);
                    if(row != null) {
                        String name = row.getCell(0).toString();
                        double numberOfItems = Double.parseDouble(row.getCell(1).toString());
                        String units = row.getCell(2).toString();
                        double price = Double.parseDouble(row.getCell(3).toString());
                        //invoiceController.add(new Item(name, numberOfItems, units, price));
                    }
                }
            } catch(Exception ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void Save(){
        if (activeFileName != null){
            System.out.println("File saved");
        }
        else{
            SaveAs();
        }

        System.out.println("File saved");
    }

    private void SaveAs(){
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            if (activeFileName == null){
                activeFileName = file.getName();
            }
            System.out.println("File saved as: " + file.getName());
        }
    }

    private void Quit(){
        if (activeFileName != null){
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save");
                alert.setHeaderText("Save changes to document “Untitled” before closing?");
                alert.setContentText("Your changes will be lost if you don’t save them.");
                ButtonType save = new ButtonType("Save");
                ButtonType saveAs = new ButtonType("SaveAs");
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(save, saveAs, cancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == save){
                    Save();
                } else if (result.get() == saveAs){
                    SaveAs();
                }
            });
            System.out.println("Quit application");
        }
    }

    public void addToInvoice(Item newItem, double quantity){
        invoiceController.add(newItem,quantity);
    }

    public void addToInvoice(List<Item> newItems, List<Double> quantities){
        int counter=0;
        for (Item newItem: newItems) {
            invoiceController.add(newItem, quantities.get(counter));
            counter++;
        }
    }

    public void addToTab(Item item){
        tab1Controller.add(item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tab1Controller.setMainController(this);
        invoiceController.setMainController(this);
    }
}
