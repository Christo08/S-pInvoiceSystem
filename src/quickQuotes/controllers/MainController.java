package quickQuotes.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
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

    private SettingsFileController settingsFileController;
    private GroupFileController groupFileController;
    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;
    private Alert popupMainUserPicker;
    private User mainUser;

    public void setStage(Stage stage) throws Exception{
        primaryStage = stage;
        fileChooser.setInitialDirectory(new File(settingsFileController.getExportPath()));
        menuItemImport.setOnAction((event) -> ImportNewDataBook());
        menuItemOpen.setOnAction((event) -> Open());
        menuItemSave.setOnAction((event) -> Save());
        menuItemSaveAs.setOnAction((event) -> SaveAs());
        menuItemQuit.setOnAction((event) -> Quit());
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
                Alert errorMessage =new Alert(Alert.AlertType.ERROR);
                try {
                    ((Stage)errorMessage.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                errorMessage.setTitle("Quick Quotes - Error");
                errorMessage.setHeaderText("Can not open file.");
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
                System.out.println("Opened file: " + activeFilePath );
                invoiceController.clearTables();
                PdfHandler pdfHandler = new PdfHandler(file, settingsFileController, invoiceController);
                pdfHandler.load();

            } catch(Exception ioe) {
                Alert errorMessage =new Alert(Alert.AlertType.ERROR);
                try {
                    ((Stage)errorMessage.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                errorMessage.setTitle("Quick Quotes - Error");
                errorMessage.setHeaderText("Can not open file.");
            }
        }
    }

    private void Save(){
        if(settingsFileController.getMainUser()==null){
            popupMainUserPicker.showAndWait();
        }
        if(settingsFileController.getMainUser()!=null) {
            if (activeFilePath != null) {
                if (settingsFileController.getMainUser() != null) {
                    File file = new File(activeFilePath);
                    if (file != null) {
                        PdfHandler pdfHandler = new PdfHandler(file, settingsFileController, invoiceController);
                        pdfHandler.save(invoiceController.getItems());
                        System.out.println("File \"" + file.getAbsolutePath() + "\" saved");
                    } else {
                        System.out.println("File not found");
                    }
                }
            } else {
                SaveAs();
            }
        }
    }

    private void createMainUserPickerPopup(){
        popupMainUserPicker = new Alert(Alert.AlertType.NONE,"Use");
        popupMainUserPicker.setTitle("Quick Quotes - Main User Picker");
        try {
            ((Stage)popupMainUserPicker.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
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
        AtomicReference<List<User>> users = new AtomicReference<>(settingsFileController.getAllUsers());
        popupMainUserPicker.setOnShown(e ->{
            users.set(settingsFileController.getAllUsers());
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
                    settingsFileController.applyChanges(map);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            usersNames.getItems().clear();
        });
    }

    private void SaveAs(){
        if(settingsFileController.getMainUser()==null){
            popupMainUserPicker.showAndWait();
        }
        if(settingsFileController.getMainUser()!=null) {
            setPdfExtensionFilter();
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                activeFilePath = file.getAbsolutePath();
                PdfHandler pdfHandler = new PdfHandler(file, settingsFileController, invoiceController);
                pdfHandler.save(invoiceController.getItems());
                System.out.println("File saved as " + file.getAbsolutePath());
            } else {
                System.out.println("File not found");
            }
        }
    }

    private void Quit(){

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quick Quotes - Save");
            try {
                ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String fileName = (activeFilePath != null)? Paths.get(activeFilePath).getFileName().toString(): "Untitled";
            alert.setHeaderText("Save changes to document “"+fileName+"” before closing?");
            alert.setContentText("Your changes will be lost if you don’t save them.");
            ButtonType save = new ButtonType("Save");
            ButtonType saveAs = new ButtonType("Save As");
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
        settingsFileController = new SettingsFileController();
        if(settingsFileController.getImportOnStartUp()){
            File file = new File(settingsFileController.getImportPath());
            ImportData(file);
        }
        createMainUserPickerPopup();
    }

    @FXML
    void showSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/Settings.fxml"));
        Parent parent = fxmlLoader.load();
        SettingsController dialogController = fxmlLoader.getController();
        dialogController.setMainController(this);

        Scene scene = new Scene(parent, 600, 400);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Settings");
        stage.getIcons().add(new Image("quickQuotes/resource/Logo.PNG"));
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void clearSheets() {
        invoiceController.clearTables();
    }

    public String getTheme() {
        return settingsFileController.getTheme();
    }

    public void refresh(Item selectedItem) {
        invoiceController.refresh(selectedItem);
    }

    public void reset() {
        invoiceController.clearTables();
        if(settingsFileController.getImportOnStartUp()){
            File file = new File(settingsFileController.getImportPath());
            ImportData(file);
        }
    }

    public void writerGroupToFile(List<Group> groups){
        groupFileController.saveGroups(groups);
    }
}
