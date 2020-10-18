package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import sample.controllers.CategoriesController;
import sample.controllers.InvoiceController;
import sample.controllers.SettingsController;
import sample.controllers.SettingsFileController;
import sample.data.Item;
import javafx.application.Platform;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.net.URL;

public class MainController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();
    private Stage primaryStage;
    private String activeFilePath = null;
    private FileChooser.ExtensionFilter excelExtensionFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
    private FileChooser.ExtensionFilter pdfExtensionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");

    @FXML
    private SplitPane invoice;

    @FXML
    private SplitPane tad1;

    @FXML
    private InvoiceController invoiceController;

    @FXML
    private CategoriesController categoriesController;

    @FXML
    private MenuItem menuItemImport;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    private MenuItem menuItemSave;

    @FXML
    private MenuItem menuItemSaveAs;

    @FXML
    private MenuItem menuItemQuit;

    private SettingsFileController settingsFileController;

    public void setStage(Stage stage) throws Exception{
        primaryStage = stage;
        menuItemImport.setOnAction((event) -> ImportNewDataBook());
        menuItemOpen.setOnAction((event) -> Open());
        menuItemSave.setOnAction((event) -> Save());
        menuItemSaveAs.setOnAction((event) -> SaveAs());
        menuItemQuit.setOnAction((event) -> Quit());
        if (settingsFileController.getImportOnStartUp())
            ImportData(new File(settingsFileController.getImportPath()));
    }

    private void setExcelExtentionFilter(){
        fileChooser.getExtensionFilters().remove(pdfExtensionFilter);
        fileChooser.getExtensionFilters().add(excelExtensionFilter);
    }

    private void setPdfExtentionFilter(){
        fileChooser.getExtensionFilters().remove(excelExtensionFilter);
        fileChooser.getExtensionFilters().add(pdfExtensionFilter);
    }


    private void addTab(String catagory, List<Item> items){
        categoriesController.addTab(catagory.subSequence(26,catagory.length()-1).toString(),items);
    }

    private void ImportNewDataBook(){
        setExcelExtentionFilter();
        File file = fileChooser.showOpenDialog(primaryStage);
        ImportData(file);
    }

    private void ImportData(File file){
        if (file.exists()) {
            try {
                XSSFWorkbook  workbook = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;


                int rows = sheet.getPhysicalNumberOfRows();
                List<Item> items = new ArrayList<>();
                String currentCategory = sheet.getRow(5).getCell(0).toString();
                for(int r = 6; r < rows - 1; r++) {
                    row = sheet.getRow(r);
                    if(row != null) {
                        String code = row.getCell(0).toString();

                        if(code.toLowerCase().contains("category"))
                        {
                            addTab(currentCategory, items);
                            currentCategory = code;
                            items = new ArrayList<>();
                        }
                        else{
                            String description = row.getCell(1).toString();
                            String unit = row.getCell(2).toString();
                            double costPrice = Double.parseDouble(row.getCell(3).toString());
                            items.add(new Item(code, description, unit, costPrice));
                        }

                    }
                }
                addTab(currentCategory, items);
                items = null;

            } catch(Exception ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void Open(){
        setPdfExtentionFilter();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file.exists()) {
            try {
                activeFilePath = file.getAbsolutePath();
                System.out.println("Opened file " + activeFilePath);
                invoiceController.clearTables();
                PdfHandler pdfHandler = new PdfHandler(file, settingsFileController, invoiceController);
                pdfHandler.load();

            } catch(Exception ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void Save(){
        if (activeFilePath != null){
            File file = new File(activeFilePath);
            if (file!= null) {
                PdfHandler pdfHandler = new PdfHandler(file, settingsFileController, invoiceController);
                pdfHandler.save(invoiceController.getItems());
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
        setPdfExtentionFilter();
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            activeFilePath = file.getAbsolutePath();
            PdfHandler pdfHandler = new PdfHandler(file, settingsFileController, invoiceController);
            pdfHandler.save(invoiceController.getItems());
            System.out.println("File saved as " + file.getAbsolutePath());
        }
        else
        {
            System.out.println("File not found");
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

    private void closeApplication(){
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriesController.setMainController(this);
        invoiceController.setMainController(this);
        settingsFileController = new SettingsFileController();
        if(settingsFileController.getImportOnStartUp()){
            File file = new File(settingsFileController.getImportPath());
            ImportData(file);
        }
    }

    @FXML
    void showSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        Parent parent = fxmlLoader.load();
        SettingsController dialogController = fxmlLoader.getController();
        dialogController.setMainController(this);

        Scene scene = new Scene(parent, 600, 400);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Settings");
        stage.getIcons().add(new Image("sample/resource/Logo.PNG"));
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void clearSheets() {
        invoiceController.clearTables();
    }

    public SettingsFileController getSettingsFileController() {
        return settingsFileController;
    }

    public String getTheme() {
        return settingsFileController.getTheme();
    }
}
