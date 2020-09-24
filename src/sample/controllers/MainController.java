package sample.controllers;

import javafx.fxml.FXML;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();
    private Stage primaryStage;
    private String activeFilePath = null;

    @FXML
    private SplitPane invoice;

    @FXML
    private SplitPane tad1;

    @FXML
    private InvoiceController invoiceController;

    @FXML
    private TabCController tabCController;

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
        if (file.exists()) {
            try {
                activeFilePath = file.getAbsolutePath();
                System.out.println("Opened file " + activeFilePath);
                invoiceController.clearTables();

                XSSFWorkbook  workbook = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;

                int rows = sheet.getPhysicalNumberOfRows();

                for(int r = 1; r < rows; r++) {
                    row = sheet.getRow(r);
                    if(row != null) {
                        String stockCode = row.getCell(0).toString();
                        String description = row.getCell(1).toString();
                        double quantity = Double.parseDouble(row.getCell(2).toString());
                        String unit = row.getCell(3).toString();
                        double sellingPrice = Double.parseDouble(row.getCell(4).toString());
                        //addToInvoice(new Item(name, numberOfItems, units, price));
                    }
                }
            } catch(Exception ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void Save(){
        if (activeFilePath != null){
            File file = new File(activeFilePath);
            if (file!= null) {
                SaveWorkbook(file);
                System.out.println("File \"" +file.getAbsolutePath()+ "\" saved");
            }
            else{
                System.out.println("File not found");
            }
        }
        else{
            SaveAs();
        }
    }

    private void SaveAs(){
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            activeFilePath = file.getAbsolutePath();
            SaveWorkbook(file);
            System.out.println("File saved as " + file.getAbsolutePath());
        }
        else
        {
            System.out.println("File not found");
        }
    }

    private void SaveWorkbook(File file){
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            addHeadingRowToSheet(sheet);
            addItemsToSheet(sheet, invoiceController.getInvoiceItems());
            workbook.write(new FileOutputStream(file));
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void Quit(){

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save");
            String fileName = (activeFilePath != null)? Paths.get(activeFilePath).getFileName().toString(): "Untitled";
            alert.setHeaderText("Save changes to document “"+fileName+"” before closing?");
            alert.setContentText("Your changes will be lost if you don’t save them.");
            ButtonType save = new ButtonType("Save");
            ButtonType saveAs = new ButtonType("SaveAs");
            ButtonType dontSave = new ButtonType("Don't save");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(save, saveAs,dontSave, cancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == save){
                Save();
                closeApplication();
            } else if (result.get() == saveAs){
                SaveAs();
                closeApplication();
            } else if (result.get() == dontSave){
                closeApplication();
            }
        });
    }

    public void addToInvoice(Item newItem, int quantity){
        invoiceController.add(newItem,quantity);
    }

    public void addToInvoice(List<Item> newItems, List<Integer> quantities){
        int counter=0;
        for (Item newItem: newItems) {
            invoiceController.add(newItem, quantities.get(counter));
            counter++;
        }
    }

    private void addHeadingRowToSheet(XSSFSheet sheet)
    {
        String[] headings = {"Stock Code", "Description", "Quantity", "Unit", "Selling Price (R)", "Total Price (R)"};

        XSSFRow row;
        row = sheet.createRow(0);
        for(int c = 0; c < headings.length; c++)
        {
            row.createCell(c).setCellValue(headings[c]);
        }
    }

    private void addItemsToSheet(XSSFSheet sheet, List<Item> items)
    {
        XSSFRow row;
        for(int i = 0; i < items.size(); i++)
        {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(items.get(i).getStockCode());
            row.createCell(1).setCellValue(items.get(i).getDescription());
            row.createCell(2).setCellValue(items.get(i).getSellingQuantityDouble());
            row.createCell(3).setCellValue(items.get(i).getUnit());
            row.createCell(4).setCellValue(items.get(i).getSellingPriceDouble());
            row.createCell(5).setCellValue(items.get(i).getTotalSellingPriceDouble());
        }
    }

    private void closeApplication(){
        Platform.exit();
        System.exit(0);
    }

    public void addToTab(Item item){
        tabCController.add(item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabCController.setMainController(this);
        invoiceController.setMainController(this);
    }

    public void clearSheets() {
        invoiceController.clearTables();
    }
}
