package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
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
    private InvoiceController invoiceController = new InvoiceController();

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    private MenuItem menuItemSave;

    @FXML
    private MenuItem menuItemSaveAs;

    @FXML
    private MenuItem menuItemQuit;

    public void setStage(Stage stage) throws Exception{
/*        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/Invoice.fxml"));
        Parent root = loader.load();
        invoiceController = (InvoiceController)loader.getController();*/
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
                //invoiceController.clearInvoice();

                XSSFWorkbook  workbook = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;
                XSSFCell cell;

                int rows = sheet.getPhysicalNumberOfRows();
                int cols = 4; // No of columns

                for(int r = 0; r < rows; r++) {
                    row = sheet.getRow(r);
                    if(row != null) {
                        for(int c = 0; c < cols; c++) {
                            cell = row.getCell((short)c);
                            if(cell != null) {
                                System.out.print(cell.toString()+"\t");
                            }
                        }
                        System.out.println();
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

    public void addToInvoice(Item newItem){
        invoiceController.add(newItem);
    }

    public void addToInvoice(List<Item> newItems){
        for (Item newItem: newItems) {
            invoiceController.add(newItem);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
