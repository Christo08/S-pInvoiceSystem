package quickQuotes.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import quickQuotes.data.Group;
import quickQuotes.data.User;
import quickQuotes.tools.PdfHandler;
import quickQuotes.data.Item;
import javafx.application.Platform;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

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

    private GroupFileController groupFileController;
    private Alert popupMainUserPicker;
    private Alert popupErrorMessage;
    private Alert popupInfoMessage;
    private Alert popupQuit;
    private Alert popupPDFLayout;
    private VBox PDFLayoutVBox;

    private CheckBox cboxQuotation;
    private CheckBox cboxCosting;
    private CheckBox cboxChecking;

    private ButtonType save;
    private ButtonType saveAs;
    private ButtonType dontSave;
    private ButtonType cancel;
    private boolean cancelSavePopup;

    public InvoiceController getInvoiceController()
    {
        return invoiceController;
    }

    public CategoriesController getCategoriesController()
    {
        return categoriesController;
    }
    public void setStage(Stage stage){
        primaryStage = stage;
        fileChooser.setInitialDirectory(new File(SettingsFileController.getExportPath()));
        menuItemImport.setOnAction((event) -> ImportNewDataBook());
        menuItemOpen.setOnAction((event) -> Open());
        menuItemSave.setOnAction((event) -> Save());
        menuItemSaveAs.setOnAction((event) -> SaveAs());
        menuItemQuit.setOnAction((event) -> Quit());
    }

    private void initializePopUps(){
        popupMainUserPicker = new Alert(Alert.AlertType.NONE,"Use");
        popupMainUserPicker.setTitle("Quick Quotes - Main User Picker");
        try {
            ((Stage)popupMainUserPicker.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupMainUserPicker.setHeaderText("Please pick a main user");
        Label text = new Label("User");
        ComboBox<String> usersNames = new ComboBox<>();
        HBox mainUserPickerHBox = new HBox(text,usersNames);
        mainUserPickerHBox.setSpacing(5);
        popupMainUserPicker.getDialogPane().setContent(mainUserPickerHBox);
        popupMainUserPicker.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        AtomicReference<List<User>> users = new AtomicReference<>(SettingsFileController.getAllUsers());
        popupMainUserPicker.setOnShown(e ->{
            users.set(SettingsFileController.getAllUsers());
            for (User user: users.get()) {
                usersNames.getItems().add(user.getName()+" "+user.getSurname());
            }
        });
        popupMainUserPicker.setOnHidden(e -> {
            if (popupMainUserPicker.getResult() == ButtonType.APPLY) {
                int mainUserIndex = usersNames.getSelectionModel().getSelectedIndex();
                users.get().get(mainUserIndex).setMainUser(true);
                HashMap<String, Object> map = new HashMap<>();
                map.put("UsersTab.Data.Users",users.get());
                try {
                    SettingsFileController.applyChanges(map);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            usersNames.getItems().clear();
        });

        popupErrorMessage =new Alert(Alert.AlertType.ERROR);
        try {
            ((Stage)popupErrorMessage.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupErrorMessage.setTitle("Quick Quotes - Error");

        popupInfoMessage =new Alert(Alert.AlertType.INFORMATION);
        try {
            ((Stage)popupInfoMessage.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupInfoMessage.setTitle("Quick Quotes - Info");


        popupQuit = new Alert(Alert.AlertType.CONFIRMATION);
        popupQuit.setTitle("Quick Quotes - Save");
        try {
            ((Stage)popupQuit.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String fileName = (activeFilePath != null)? Paths.get(activeFilePath).getFileName().toString(): "Untitled";
        popupQuit.setHeaderText("Save changes to document “"+fileName+"” before closing?");
        popupQuit.setContentText("Your changes will be lost if you don’t save them.");
        save = new ButtonType("Save");
        saveAs = new ButtonType("Save As");
        dontSave = new ButtonType("Don't save");
        cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        popupQuit.getButtonTypes().setAll(save, saveAs,dontSave, cancel);

        PDFLayoutVBox = new VBox();
        cboxQuotation = new CheckBox("Add Quotation Sheet");
        cboxCosting = new CheckBox("Add Costing Sheet");
        cboxChecking = new CheckBox("Add Checking Sheet");
        PDFLayoutVBox.getChildren().addAll(cboxQuotation,cboxCosting,cboxChecking);

        popupPDFLayout = new Alert(Alert.AlertType.NONE);
        popupPDFLayout.setTitle("Quick Quotes - PDF Layout");
        try {
            ((Stage)popupPDFLayout.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupPDFLayout.setHeaderText("PDF Layout");
        popupPDFLayout.getDialogPane().setContent(PDFLayoutVBox);
        popupPDFLayout.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupPDFLayout.setOnShown(e ->{
            boolean checkQuotation = SettingsFileController.getQuotationPositions().values()
                                                                                    .stream()
                                                                                    .anyMatch(positions -> positions != -1);
            boolean checkCosting = SettingsFileController.getCostingPositions().values()
                                                                                .stream()
                                                                                .anyMatch(positions -> positions != -1);
            boolean checkChecking = SettingsFileController.getCheckingPositions().values()
                                                                                  .stream()
                                                                                  .anyMatch(positions -> positions != -1);
            cboxQuotation.setSelected(checkQuotation);
            cboxCosting.setSelected(checkCosting);
            cboxChecking.setSelected(checkChecking);
        });
        popupPDFLayout.setOnHidden(e -> {
            if (popupPDFLayout.getResult() == ButtonType.APPLY) {
                if(!isCheckingSheetNeeded() && !isCostingSheetNeeded() && !isQuotationSheetNeeded()) {
                    cancelSavePopup = false;
                    popupErrorMessage.setHeaderText("Please select a sheet.");
                    popupErrorMessage.showAndWait();
                }
                else {
                    cancelSavePopup = true;
                }
            }
            else
            {
                cancelSavePopup=false;
            }
        });
    }

    private void setExcelExtensionFilter(){
        fileChooser.getExtensionFilters().remove(pdfExtensionFilter);
        fileChooser.getExtensionFilters().add(excelExtensionFilter);
    }

    private void setPdfExtensionFilter(){
        fileChooser.getExtensionFilters().remove(excelExtensionFilter);
        fileChooser.getExtensionFilters().add(pdfExtensionFilter);
    }

    private void addTab(String category, List<Item> items){
        categoriesController.addTab(category.subSequence(26,category.length()-1).toString(),items);
    }

    private void addGroups(){
        categoriesController.addGroups(groupFileController.getGroups());
    }

    private void ImportNewDataBook(){
        setExcelExtensionFilter();
        File file = fileChooser.showOpenDialog(primaryStage);
        ImportData(file);
    }

    private void ImportData(File file){
        if (file!=null&&file.exists()) {
            try {
                XSSFWorkbook  workbook = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;


                int rows = sheet.getPhysicalNumberOfRows();
                List<Item> allItems = new ArrayList<>();
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
                            allItems.add(new Item(code, description, unit, costPrice));
                        }

                    }
                }
                addTab(currentCategory, items);
                groupFileController = new GroupFileController(allItems);
                addGroups();

            } catch(Exception ioe) {
                popupErrorMessage.setHeaderText("Can not import data.");
                popupErrorMessage.showAndWait();
            }
        }
    }

    private void Open(){
        setPdfExtensionFilter();
        File file = fileChooser.showOpenDialog(primaryStage);
        fileChooser.setInitialDirectory(null);
        if (file!=null&&file.exists()) {
            try {
                activeFilePath = file.getAbsolutePath();
                invoiceController.clearTables();
                PdfHandler pdfHandler = new PdfHandler(file, this);
                if(pdfHandler.load())
                {
                    popupInfoMessage.setHeaderText("Successfully opened file "+file.getName()+".");
                    popupInfoMessage.showAndWait();
                }
                else
                {
                    popupErrorMessage.setHeaderText("Error loading from file \"" + file.getName() + "\"");
                    popupErrorMessage.showAndWait();
                }

            } catch(Exception ioe) {
                popupErrorMessage.setHeaderText(ioe.getMessage());
                popupErrorMessage.showAndWait();
            }
        }
    }

    private void Save(){
        if (SettingsFileController.getMainUser() == null) {
            popupMainUserPicker.showAndWait();
        }
        if (SettingsFileController.getMainUser() != null) {
            if(invoiceController.getItems().size() > 0) {
                if (activeFilePath != null) {
                    popupPDFLayout.showAndWait();
                    if (cancelSavePopup) {
                        if (SettingsFileController.getMainUser() != null) {
                            File file = new File(activeFilePath);
                            if (file != null) {
                                PdfHandler pdfHandler = new PdfHandler(file, this);
                                try {
                                    pdfHandler.save(invoiceController.getItems());
                                } catch (Exception exception) {
                                    popupErrorMessage.setHeaderText(exception.toString());
                                }
                                popupInfoMessage.setHeaderText("Successfully saved.");
                                popupInfoMessage.showAndWait();
                            } else {
                                popupErrorMessage.setHeaderText("File not found.");
                                popupErrorMessage.showAndWait();
                            }
                        }
                    }
                } else {
                    SaveAs();
                }
            }else{
                popupErrorMessage.setHeaderText("Please add a item to the quotation");
                popupErrorMessage.showAndWait();
            }
        }
    }

    private void SaveAs(){
        if(SettingsFileController.getMainUser()==null){
            popupMainUserPicker.showAndWait();
        }
        if(SettingsFileController.getMainUser()!=null) {
            if(invoiceController.getItems().size() > 0) {
                popupPDFLayout.showAndWait();
                if(cancelSavePopup) {
                    setPdfExtensionFilter();
                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        activeFilePath = file.getAbsolutePath();
                        PdfHandler pdfHandler = new PdfHandler(file, this);
                        try {
                            pdfHandler.save(invoiceController.getItems());
                        } catch (Exception exception) {
                            popupErrorMessage.setHeaderText(exception.toString());
                        }
                        popupInfoMessage.setHeaderText("File saved as " + file.getName() + ".");
                        popupInfoMessage.showAndWait();
                    } else {
                        popupErrorMessage.setHeaderText("File not found.");
                        popupErrorMessage.show();
                    }
                }
            }else{
                popupErrorMessage.setHeaderText("Please add a item to the quotation");
                popupErrorMessage.showAndWait();
            }
        }
    }

    private void Quit(){

        Platform.runLater(() -> {
            Optional<ButtonType> result = popupQuit.showAndWait();
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

    public void addToInvoice(List<Item> newItems, List<Integer> quantities){
        int counter=0;
        for (Item newItem: newItems) {
            invoiceController.add(newItem, quantities.get(counter));
            counter++;
        }
    }

    public void addToInvoice(Item newItem, int quantity) {
        invoiceController.add(newItem, quantity);
    }

    private void closeApplication(){
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriesController.setMainController(this);
        invoiceController.setMainController(this);
        if(SettingsFileController.getImportOnStartUp()){
            File file = new File(SettingsFileController.getImportPath());
            ImportData(file);
        }
        initializePopUps();
    }

    @FXML
    void showSettings() throws IOException {
        URL fxmlURL = Paths.get(SettingsFileController.getFXMLSettingsPath()).toUri().toURL();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxmlURL);
        Parent parent = fxmlLoader.load();
        SettingsController dialogController = fxmlLoader.getController();
        dialogController.setMainController(this);

        Scene scene = new Scene(parent, 600, 400);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Settings");
        stage.getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void clearSheets() {
        invoiceController.clearTables();
    }

    public void reset() {
        invoiceController.clearTables();
        if(SettingsFileController.getImportOnStartUp()){
            File file = new File(SettingsFileController.getImportPath());
            ImportData(file);
        }
    }

    public void writerGroupToFile(List<Group> groups){
        groupFileController.saveGroups(groups);
    }

    public boolean isQuotationSheetNeeded() {
        return cboxQuotation.isSelected();
    }

    public boolean isCostingSheetNeeded() {
        return cboxCosting.isSelected();
    }

    public boolean isCheckingSheetNeeded() {
        return cboxChecking.isSelected();
    }
}
