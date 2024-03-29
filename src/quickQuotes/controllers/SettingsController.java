package quickQuotes.controllers;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import quickQuotes.data.User;
import quickQuotes.tools.ChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SettingsController implements Initializable {

    @FXML
    private BorderPane BorderPaneSettings;

    @FXML
    private Tab UsersTab;

    @FXML
    private ListView<User> LVUsersList;

    @FXML
    private TextField TxtNameInput;

    @FXML
    private TextField TxtSurnameInput;

    @FXML
    private TextField TxtNumberInput;

    @FXML
    private TextField TxtEmailInput;

    @FXML
    private Button btnChangesUser;

    @FXML
    private Button btnResetUser;

    @FXML
    private CheckBox CheckMakeMainUsers;

    @FXML
    private Tab PDFTab;

    @FXML
    private ComboBox<String> CbxQuotationInfoPosition;

    @FXML
    private ComboBox<String> CbxQuotationTablePosition;

    @FXML
    private ComboBox<String> CbxQuotationTextPosition;

    @FXML
    private TextArea TxtAreaQuotationTextInput;

    @FXML
    private ComboBox<String> CbxCostSheetInfoPosition;

    @FXML
    private ComboBox<String> CbxCostSheetTablePosition;

    @FXML
    private ComboBox<String> CbxCostSheetTextPosition;

    @FXML
    private TextArea TxtAreaCostSheetTextInput;

    @FXML
    private ComboBox<String> CbxCheckingSheetInfoPosition;

    @FXML
    private ComboBox<String> CbxCheckingSheetTablePosition;

    @FXML
    private ComboBox<String> CbxCheckingSheetTextPosition;

    @FXML
    private TextArea TxtAreaCheckingSheetTextInput;

    @FXML
    private Tab PathsTab;

    @FXML
    private RadioButton RBtnImportStartUpYes;

    @FXML
    private ToggleGroup importOnStartUp;

    @FXML
    private RadioButton RBtnImportStartUpNo;

    @FXML
    private TextField TxtImportPath;

    @FXML
    private Button btnImportPath;

    @FXML
    private TextField TxtExportPath;

    @FXML
    private Button btnExportPath;

    @FXML
    private Button btnApply;

    @FXML
    private HBox UserControlHBox;

    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioButton rbDark;

    @FXML
    private RadioButton rbLight;

    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final FileChooser fileChooser = new FileChooser();
    private FileChooser.ExtensionFilter excelExtensionFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");

    private MainController mainController;

    private ChangeListener changeListener;
    private Stage primaryStage;
    private ObservableList<User>  listUser;

    private ContextMenu contextMenu;
    private User selectUser;

    Pattern phonePatterns = Pattern.compile("(?:\\(\\d{3}\\)|\\d{3}([-]|[\\s])*)\\d{3}([-]|[\\s])*\\d{4}");
    private final String emailPattern= "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    public static String CSSPath = new File("src/quickQuotes/CSS/").getAbsolutePath();

    private Image logo;
    private boolean isMakeAUser;

    public SettingsController() {
        contextMenu = new ContextMenu();
        isMakeAUser = false;
        try {
            logo =new Image(new FileInputStream(SettingsFileController.getLogoPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void changesCostingsSheetInfoPosition(ActionEvent event) {
        CbxCostSheetInfoPosition.setStyle("");
        CbxCostSheetTablePosition.setStyle("");
        CbxCostSheetTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxCostSheetInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxCostSheetTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxCostSheetTextPosition.getSelectionModel().getSelectedItem());
        if (infoPosition==-1){
            changeListener.replace("PDFTab.Data.CostingSheet.Position.info",-1);
        }else if(tablePosition==infoPosition){
            CbxCostSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCostSheetTablePosition.setStyle("-fx-border-color: red");
        }else if(textPosition==infoPosition){
            CbxCostSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCostSheetTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.CostingSheet.Position.info",infoPosition);
        }
    }

    @FXML
    void changesCostingsSheetTablePosition(ActionEvent event) {
        CbxCostSheetInfoPosition.setStyle("");
        CbxCostSheetTablePosition.setStyle("");
        CbxCostSheetTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxCostSheetInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxCostSheetTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxCostSheetTextPosition.getSelectionModel().getSelectedItem());
        if (tablePosition==-1){
            changeListener.replace("PDFTab.Data.CostingSheet.Position.info",-1);
            changeListener.replace("PDFTab.Data.CostingSheet.Position.table",-1);
            changeListener.replace("PDFTab.Data.CostingSheet.Position.text",-1);
            CbxCostSheetInfoPosition.setValue(CbxCostSheetInfoPosition.getItems().get(0));
            CbxCostSheetTablePosition.setValue(CbxCostSheetTablePosition.getItems().get(0));
            CbxCostSheetTextPosition.setValue(CbxCostSheetTextPosition.getItems().get(0));
            TxtAreaCostSheetTextInput.setDisable(true);
            disableEnableChecking(true);
        }else if(tablePosition==infoPosition){
            CbxCostSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCostSheetTablePosition.setStyle("-fx-border-color: red");
        }else if(textPosition==tablePosition){
            CbxCostSheetTablePosition.setStyle("-fx-border-color: red");
            CbxCostSheetTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.CostingSheet.Position.table",tablePosition);
        }
    }

    @FXML
    void changesCostSheetTextPosition(ActionEvent event) {
        CbxCostSheetInfoPosition.setStyle("");
        CbxCostSheetTablePosition.setStyle("");
        CbxCostSheetTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxCostSheetInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxCostSheetTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxCostSheetTextPosition.getSelectionModel().getSelectedItem());
        if (textPosition==-1){
            changeListener.replace("PDFTab.Data.CostingSheet.Position.text",-1);
            TxtAreaCostSheetTextInput.setDisable(true);
        }else if(textPosition==infoPosition){
            CbxCostSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCostSheetTextPosition.setStyle("-fx-border-color: red");
        }else if(textPosition==tablePosition){
            CbxCostSheetTablePosition.setStyle("-fx-border-color: red");
            CbxCostSheetTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.CostingSheet.Position.text",textPosition);
            TxtAreaCostSheetTextInput.setDisable(false);
        }
    }

    @FXML
    void changesQuotationTextPosition(ActionEvent event) {
        CbxQuotationInfoPosition.setStyle("");
        CbxQuotationTablePosition.setStyle("");
        CbxQuotationTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxQuotationInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxQuotationTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxQuotationTextPosition.getSelectionModel().getSelectedItem());
        if (textPosition==-1){
            changeListener.replace("PDFTab.Data.Quotation.Position.text",-1);
            TxtAreaQuotationTextInput.setDisable(true);
        }else if(textPosition==infoPosition){
            CbxQuotationInfoPosition.setStyle("-fx-border-color: red");
            CbxQuotationTextPosition.setStyle("-fx-border-color: red");
        }else if(textPosition==tablePosition){
            CbxQuotationTablePosition.setStyle("-fx-border-color: red");
            CbxQuotationTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.Quotation.Position.text",textPosition);
            TxtAreaQuotationTextInput.setDisable(false);
        }
    }

    @FXML
    void changesQuotationTablePosition(ActionEvent event) {
        CbxQuotationInfoPosition.setStyle("");
        CbxQuotationTablePosition.setStyle("");
        CbxQuotationTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxQuotationInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxQuotationTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxQuotationTextPosition.getSelectionModel().getSelectedItem());
        if (tablePosition==-1){
            changeListener.replace("PDFTab.Data.Quotation.Position.info",-1);
            changeListener.replace("PDFTab.Data.Quotation.Position.table",-1);
            changeListener.replace("PDFTab.Data.Quotation.Position.text",-1);
            CbxQuotationInfoPosition.setValue(CbxQuotationInfoPosition.getItems().get(0));
            CbxQuotationTablePosition.setValue(CbxQuotationTablePosition.getItems().get(0));
            CbxQuotationTextPosition.setValue(CbxQuotationTextPosition.getItems().get(0));
            TxtAreaQuotationTextInput.setDisable(true);
            disableEnableChecking(true);
        }else if(tablePosition==infoPosition){
            CbxQuotationInfoPosition.setStyle("-fx-border-color: red");
            CbxQuotationTablePosition.setStyle("-fx-border-color: red");
        }else if(textPosition==tablePosition){
            CbxQuotationTablePosition.setStyle("-fx-border-color: red");
            CbxQuotationTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.Quotation.Position.table",tablePosition);
        }
    }

    @FXML
    void changesQuotationInfoPosition(ActionEvent event) {
        CbxQuotationInfoPosition.setStyle("");
        CbxQuotationTablePosition.setStyle("");
        CbxQuotationTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxQuotationInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxQuotationTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxQuotationTextPosition.getSelectionModel().getSelectedItem());
        if (infoPosition==-1){
            changeListener.replace("PDFTab.Data.Quotation.Position.info",-1);
        }else if(tablePosition==infoPosition){
            CbxQuotationInfoPosition.setStyle("-fx-border-color: red");
            CbxQuotationTablePosition.setStyle("-fx-border-color: red");
        }else if(textPosition==infoPosition){
            CbxQuotationInfoPosition.setStyle("-fx-border-color: red");
            CbxQuotationTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.Quotation.Position.info",infoPosition);
        }
    }

    @FXML
    void changesCheckingSheetInfoPosition(ActionEvent event) {
        CbxCheckingSheetInfoPosition.setStyle("");
        CbxCheckingSheetTablePosition.setStyle("");
        CbxCheckingSheetTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxCheckingSheetInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxCheckingSheetTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxCheckingSheetTextPosition.getSelectionModel().getSelectedItem());
        if (infoPosition==-1){
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.info",-1);
        }else if(tablePosition==infoPosition){
            CbxCheckingSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCheckingSheetTablePosition.setStyle("-fx-border-color: red");
        }else if(textPosition==infoPosition){
            CbxCheckingSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCheckingSheetTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.info",infoPosition);
        }
    }

    @FXML
    void changesCheckingSheetTablePosition(ActionEvent event) {
        CbxCheckingSheetInfoPosition.setStyle("");
        CbxCheckingSheetTablePosition.setStyle("");
        CbxCheckingSheetTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxCheckingSheetInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxCheckingSheetTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxCheckingSheetTextPosition.getSelectionModel().getSelectedItem());
        if (tablePosition==-1){
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.info",-1);
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.table",-1);
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.text",-1);
            CbxCheckingSheetInfoPosition.setValue(CbxCheckingSheetInfoPosition.getItems().get(0));
            CbxCheckingSheetTablePosition.setValue(CbxCheckingSheetTablePosition.getItems().get(0));
            CbxCheckingSheetTextPosition.setValue(CbxCheckingSheetTextPosition.getItems().get(0));
            TxtAreaCheckingSheetTextInput.setDisable(true);
            disableEnableChecking(true);
        }else if(tablePosition==infoPosition){
            CbxCheckingSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCheckingSheetTablePosition.setStyle("-fx-border-color: red");
        }else if(textPosition==tablePosition){
            CbxCheckingSheetTablePosition.setStyle("-fx-border-color: red");
            CbxCheckingSheetTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.table",tablePosition);
        }
    }

    @FXML
    void changesCheckingSheetTextPosition(ActionEvent event) {
        CbxCheckingSheetInfoPosition.setStyle("");
        CbxCheckingSheetTablePosition.setStyle("");
        CbxCheckingSheetTextPosition.setStyle("");
        int infoPosition = positionsStringToInteger(CbxCheckingSheetInfoPosition.getSelectionModel().getSelectedItem());
        int tablePosition = positionsStringToInteger(CbxCheckingSheetTablePosition.getSelectionModel().getSelectedItem());
        int textPosition = positionsStringToInteger(CbxCheckingSheetTextPosition.getSelectionModel().getSelectedItem());
        if (textPosition==-1){
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.text",-1);
            TxtAreaCheckingSheetTextInput.setDisable(true);
        }else if(textPosition==infoPosition){
            CbxCheckingSheetInfoPosition.setStyle("-fx-border-color: red");
            CbxCheckingSheetTextPosition.setStyle("-fx-border-color: red");
        }else if(textPosition==tablePosition){
            CbxCheckingSheetTablePosition.setStyle("-fx-border-color: red");
            CbxCheckingSheetTextPosition.setStyle("-fx-border-color: red");
        }else {
            changeListener.replace("PDFTab.Data.CheckingSheet.Position.text",textPosition);
            TxtAreaCheckingSheetTextInput.setDisable(false);
        }
    }

    @FXML
    void applyChanges(ActionEvent event) {
            try {
                SettingsFileController.applyChanges(changeListener.getNewValues());
                btnApply.setDisable(true);
                changeListener.update(SettingsFileController.getKeyValuePair());
            } catch (IOException e) {
                Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                errorDialog.setTitle("Quick Quotes - Save Error");
                ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
                errorDialog.setHeaderText("Can not save the settings.");
                errorDialog.setContentText(e.toString());
                errorDialog.showAndWait();
            }
    }

    @FXML
    void closePopUp(ActionEvent event) {
        closeStage(event);
    }

    @FXML
    void changesUsers(ActionEvent event) {
        String name =TxtNameInput.getText();
        String surname =TxtSurnameInput.getText();
        String number =TxtNumberInput.getText();
        String email = TxtEmailInput.getText();
        boolean isMainUser = CheckMakeMainUsers.isSelected();
        if(!TxtNameInput.getStyle().isEmpty()||!TxtSurnameInput.getStyle().isEmpty()){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Quick Quotes - Error");
            ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            errorDialog.setTitle("Change user");
            errorDialog.setHeaderText("Invalid name and surname.");
            errorDialog.setContentText("This user is in the list. Change the name or surname to a valid email.");
            errorDialog.showAndWait();
            return;
        }
        if(!TxtNumberInput.getStyle().isEmpty()){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Quick Quotes - Error");
            ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            errorDialog.setHeaderText("Invalid number.");
            errorDialog.setContentText("Change the phone number to a valid phone number.");
            errorDialog.showAndWait();
            return;
        }
        if(!TxtEmailInput.getStyle().isEmpty()){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Quick Quotes - Error");
            ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            errorDialog.setHeaderText("Invalid email.");
            errorDialog.setContentText("Change the email to a valid email.");
            errorDialog.showAndWait();
            return;
        }
        listUser.remove(selectUser);
        selectUser.setName(name);
        selectUser.setSurname(surname);
        selectUser.setNumber(number);
        selectUser.setEmail(email);
        selectUser.setMainUser(isMainUser);
        if (isMainUser){
            for (User user: listUser) {
                if(!selectUser.getId().equals(user.getId())){
                    user.setMainUser(false);
                }
            }
            selectMainUser(false);
        }
        listUser.add(selectUser);
        btnChangesUser.setDisable(true);
        btnResetUser.setDisable(true);
    }

    @FXML
    void disableEnableImportPathTextField(ActionEvent event) {
        if(RBtnImportStartUpYes.isSelected()){
            TxtImportPath.setDisable(false);
            btnImportPath.setDisable(false);
            changeListener.replace("PathsTab.Data.ImportOnStartUp",true);
        }else{
            TxtImportPath.setDisable(true);
            btnImportPath.setDisable(true);
            changeListener.replace("PathsTab.Data.ImportOnStartUp",false);
        }
    }

    @FXML
    void showFileChooser(ActionEvent event) {
        File file = fileChooser.showOpenDialog(primaryStage);
        if(file != null){
            TxtImportPath.setText(file.getPath());
            changeListener.replace("PathsTab.Data.Import",file.getPath());
        }
    }

    @FXML
    void showFolderChooser(ActionEvent event) {
        File folder =directoryChooser.showDialog(primaryStage);
        if(folder != null){
            TxtExportPath.setText(folder.getPath());
            changeListener.replace("PathsTab.Data.Export",folder.getPath());
        }

    }

    @FXML
    void resetUser(ActionEvent event){
        List<User> copyOfUsers = new ArrayList<>();
        for(User user: SettingsFileController.getAllUsers()){
            copyOfUsers.add(new User(user));
        }
        listUser= FXCollections.observableArrayList(copyOfUsers);
        LVUsersList.setItems(listUser);
        TxtNameInput.setText("");
        TxtSurnameInput.setText("");
        TxtEmailInput.setText("");
        TxtNumberInput.setText("");
        TxtNameInput.setStyle("");
        TxtSurnameInput.setStyle("");
        TxtEmailInput.setStyle("");
        TxtNumberInput.setStyle("");
        CheckMakeMainUsers.setSelected(false);
        selectMainUser(false);
        btnChangesUser.setDisable(true);
        btnResetUser.setDisable(true);
        changeListener.replace("UsersTab.Data.Users",null);
        changeListener.replace("UsersTab.Data.UsersCounter",null);
    }

    @FXML
    void changeMainUser(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMainController(MainController mainController) {
        this.mainController=mainController;
        changeListener = new ChangeListener(this, SettingsFileController.getKeyValuePair());

        setUpEnableOrDisableTabs();
        setUpDataInTabs();
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        fileChooser.getExtensionFilters().add(excelExtensionFilter);
        fileChooser.setTitle("Get File Path");
        directoryChooser.setTitle("Get Folder Path");
    }

    private void setUpDataInTabs() {
        setUpDataPDF();
        setUpDataPaths();
        setUpDataUsers();
        setUpDataTheme();
    }

    private void setUpDataTheme() {
        if(SettingsFileController.getTheme().contains("dark")){
            rbDark.setSelected(true);
            rbLight.setSelected(false);
        }else{
            rbLight.setSelected(true);
            rbDark.setSelected(false);
        }
        themeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            String file1 =((RadioButton)newValue).getText().toLowerCase()+"Theme.css";
            String file2 =((RadioButton)oldValue).getText().toLowerCase()+"Theme.css";
            String absoluteCSSPath1= SettingsFileController.getCSSPath()+"\\"+ file1;
            String absoluteCSSPath2 = SettingsFileController.getCSSPath()+"\\"+ file2;
            File f1 = new File(absoluteCSSPath1);
            File f2 = new File(absoluteCSSPath2);
            String finalFilePath1="file:///" + f1.getAbsolutePath().replace("\\", "/");
            String finalFilePath2="file:///" + f2.getAbsolutePath().replace("\\", "/");
            Platform.runLater(()->{
                StyleManager.getInstance().removeUserAgentStylesheet(finalFilePath2);
                StyleManager.getInstance().addUserAgentStylesheet(finalFilePath1);
                changeListener.replace("ThemeTab.Theme",file1);
            });
        });
    }

    private void setUpDataUsers() {
        List<User> copyOfUsers = new ArrayList<>();
        for(User user: SettingsFileController.getAllUsers()){
            copyOfUsers.add(new User(user));
        }
        listUser= FXCollections.observableArrayList(copyOfUsers);
        listUser.addListener((ListChangeListener<User>) changes -> {
            List<User> list =  listUser.stream().collect(Collectors.toList());
            Collections.sort(list, Comparator.comparing(User::toString));
            changeListener.replace("UsersTab.Data.Users",list);
            changeListener.replace("UsersTab.Data.UsersCounter",list.size());
        });
        listUser.removeListener((ListChangeListener<User>) changes -> {
            List<User> list =  listUser.stream().collect(Collectors.toList());
            Collections.sort(list, Comparator.comparing(User::toString));
            changeListener.replace("UsersTab.Data.Users",list);
            changeListener.replace("UsersTab.Data.UsersCounter",list.size());
        });
        if(listUser.size()==0)
        {
            TxtNameInput.setDisable(true);
            TxtSurnameInput.setDisable(true);
            TxtEmailInput.setDisable(true);
            TxtNumberInput.setDisable(true);
            CheckMakeMainUsers.setDisable(true);
        }
        LVUsersList.setItems(listUser);
        LVUsersList.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null || user.getName() == null|| user.getSurname() == null) {
                    setText(null);
                } else {
                    setText(user.getName()+" "+user.getSurname());
                }
            }
        });
        LVUsersList.setOnMouseClicked(event -> {
            isMakeAUser =false;
            showUserData(LVUsersList.getSelectionModel().getSelectedItem());
            btnChangesUser.setDisable(true);
            btnResetUser.setDisable(true);
        });
        selectMainUser(true);
        TxtNameInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                boolean nameCheck;
                boolean surname;
                boolean sameUser;
                for(User user:listUser){
                    nameCheck = user.getName().equals(TxtNameInput.getText());
                    surname=user.getSurname().equals(TxtSurnameInput.getText());
                    sameUser=!user.getId().equals(selectUser.getId());
                    if(nameCheck&&surname&&sameUser){
                        TxtNameInput.setStyle("-fx-border-color: red");
                        TxtSurnameInput.setStyle("-fx-border-color: red");
                        btnChangesUser.setDisable(false);
                        btnResetUser.setDisable(false);
                    }
                }
                TxtNameInput.setStyle("");
                TxtSurnameInput.setStyle("");
                for (User user: listUser) {
                    if(user.getId().equals(selectUser.getId())){
                        if(SettingsFileController.isUsersTabChangesButtonEnable()) {
                            btnChangesUser.setDisable(false);
                            btnResetUser.setDisable(false);
                        }
                    }
                }
            }
        });
        TxtSurnameInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                boolean nameCheck;
                boolean surname;
                boolean sameUser;
                for(User user:listUser){
                    nameCheck = user.getName().equals(TxtNameInput.getText());
                    surname=user.getSurname().equals(TxtSurnameInput.getText());
                    sameUser=!user.getId().equals(selectUser.getId());
                    if(nameCheck&&surname&&sameUser){
                        TxtNameInput.setStyle("-fx-border-color: red");
                        TxtSurnameInput.setStyle("-fx-border-color: red");
                        btnChangesUser.setDisable(false);
                        btnResetUser.setDisable(false);
                    }
                }
                TxtNameInput.setStyle("");
                TxtSurnameInput.setStyle("");
                for (User user: listUser) {
                    if(user.getId().equals(selectUser.getId())){
                        if(SettingsFileController.isUsersTabChangesButtonEnable()) {
                            btnChangesUser.setDisable(false);
                            btnResetUser.setDisable(false);
                        }
                    }
                }
            }
        });
        TxtEmailInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue && selectUser!=null){
                String newEmail = TxtEmailInput.getText();
                Pattern pattern = Pattern.compile(emailPattern);
                if(pattern.matcher(newEmail).matches()) {
                    for (User user: listUser) {
                        if(user.getId().equals(selectUser.getId())){
                            if(SettingsFileController.isUsersTabChangesButtonEnable()) {
                                btnChangesUser.setDisable(false);
                                btnResetUser.setDisable(false);
                            }
                        }
                    }
                    TxtEmailInput.setStyle("");
                }else{
                    TxtEmailInput.setStyle("-fx-border-color: red");
                    btnChangesUser.setDisable(false);
                    btnResetUser.setDisable(false);
                }
            }
        });
        TxtNumberInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue && selectUser!=null){
                String newNumber = TxtNumberInput.getText();
                if(phonePatterns.matcher(newNumber).matches()) {
                    for (User user : listUser) {
                        if (user.getId().equals(selectUser.getId())) {
                            if (SettingsFileController.isUsersTabChangesButtonEnable()) {
                                btnChangesUser.setDisable(false);
                                btnResetUser.setDisable(false);
                            }
                        }
                    }
                    TxtNumberInput.setStyle("");
                }else{
                    TxtNumberInput.setStyle("-fx-border-color: red");
                    btnChangesUser.setDisable(false);
                    btnResetUser.setDisable(false);
                }
            }
        });
        CheckMakeMainUsers.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!isMakeAUser){
                btnChangesUser.setDisable(false);
                btnResetUser.setDisable(false);
            }
        });

        MenuItem addNewUserMenuItem = new MenuItem("Add new user");
        Button btnDone = new Button("Done");
        btnDone.setOnAction(event1 -> {
            createUser();
            isMakeAUser =false;
        });
        addNewUserMenuItem.setOnAction(event->{
            isMakeAUser =true;
            showUserData(null);
            TxtNameInput.setDisable(false);
            TxtSurnameInput.setDisable(false);
            TxtEmailInput.setDisable(false);
            TxtNumberInput.setDisable(false);
            CheckMakeMainUsers.setDisable(false);
            UserControlHBox.getChildren().add(btnDone);
        });
        MenuItem removeUserMenuItem = new MenuItem("Remove user");
        removeUserMenuItem.setOnAction(event->{
            User user= LVUsersList.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quick Quotes - Remove User");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            alert.setHeaderText("Remove User");
            alert.setContentText("Are you sure you want to remove "+user.getName()+" "+user.getSurname()+"?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                if(user.isMainUser()){
                    Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                    alertWarning.setTitle("Quick Quotes -Remove User");
                    ((Stage)alertWarning.getDialogPane().getScene().getWindow()).getIcons().add(logo);
                    alertWarning.setHeaderText("Can not remove user");
                    alertWarning.setContentText("Can not remove "+user.getName()+" "+user.getSurname()+", because it is the main user");
                    alertWarning.showAndWait();
                }else{
                    btnResetUser.setDisable(false);
                    listUser.remove(user);
                    List<User> list =  listUser.stream().collect(Collectors.toList());;
                    changeListener.replace("UsersTab.Data.Users",list);
                    selectMainUser(false);
                }
            }
        });
        if(SettingsFileController.isUsersTabAddButtonEnable()){
            contextMenu.getItems().add(addNewUserMenuItem);
        }
        contextMenu.showingProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().remove(removeUserMenuItem);
            if(SettingsFileController.isUsersTabRemoveButtonEnable()&&LVUsersList.getSelectionModel().getSelectedItem()!=null){
                contextMenu.getItems().add(removeUserMenuItem);
            }
        });
        LVUsersList.setContextMenu(contextMenu);
    }

    private void selectMainUser(boolean pAdded) {
        if(listUser.size() !=0 || pAdded) {
            for (User user : listUser) {
                if (user.isMainUser()) {
                    Platform.runLater(() -> {
                        LVUsersList.scrollTo(LVUsersList.getItems().indexOf(user));
                        LVUsersList.getSelectionModel().select(LVUsersList.getItems().indexOf(user));
                        showUserData(user);
                        btnChangesUser.setDisable(true);
                        btnResetUser.setDisable(true);
                    });
                }
            }
        } else{
            showUserData(null);
        }
    }

    private void createUser() {
        String name =TxtNameInput.getText();
        String surname =TxtSurnameInput.getText();
        String number =TxtNumberInput.getText();
        String email = TxtEmailInput.getText();
        boolean isMainUser = CheckMakeMainUsers.isSelected();
        if(!TxtNumberInput.getStyle().isEmpty()){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Quick Quotes - Error");
            ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            errorDialog.setHeaderText("Invalid number.");
            errorDialog.setContentText("Change the phone number to a valid phone number.");
            errorDialog.showAndWait();
            return;
        }
        if(!TxtEmailInput.getStyle().isEmpty()){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Quick Quotes -Error");
            ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            errorDialog.setHeaderText("Invalid email.");
            errorDialog.setContentText("Change the email to a valid email.");
            errorDialog.showAndWait();
            return;
        }
        if(!TxtNameInput.getStyle().isEmpty()||!TxtSurnameInput.getStyle().isEmpty()){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Quick Quotes - Error");
            ((Stage)errorDialog.getDialogPane().getScene().getWindow()).getIcons().add(logo);
            errorDialog.setHeaderText("Invalid name and surname.");
            errorDialog.setContentText("This user is in the list. Change the name or surname to a valid email.");
            errorDialog.showAndWait();
            return;
        }
        selectUser.setName(name);
        selectUser.setSurname(surname);
        selectUser.setNumber(number);
        selectUser.setEmail(email);
        selectUser.setMainUser(isMainUser);
        selectUser.setId(listUser.size()+1);
        TxtNameInput.clear();
        TxtSurnameInput.clear();
        TxtNumberInput.clear();
        TxtEmailInput.clear();
        UserControlHBox.getChildren().remove(2);
        if (isMainUser){
            for (User user: listUser) {
                if(!selectUser.getId().equals(user.getId())){
                    user.setMainUser(false);
                }
            }
            selectMainUser(true);
        }
        listUser.add(selectUser);
        LVUsersList.getSelectionModel().select(selectUser);
        showUserData(LVUsersList.getSelectionModel().getSelectedItem());
        btnChangesUser.setDisable(true);
        btnResetUser.setDisable(true);
        btnResetUser.setDisable(false);
    }

    private void showUserData(User user) {
        if(UserControlHBox.getChildren().size()==3)
            UserControlHBox.getChildren().remove(2);
        if(user!=null) {
            selectUser=user;
            TxtNameInput.setText(user.getName());
            TxtSurnameInput.setText(user.getSurname());
            TxtNumberInput.setText(user.getNumber());
            TxtEmailInput.setText(user.getEmail());
            CheckMakeMainUsers.setSelected(user.isMainUser());
        }else{
            selectUser=new User();
            TxtNameInput.setText("");
            TxtSurnameInput.setText("");
            TxtNumberInput.setText("");
            TxtEmailInput.setText("");
            CheckMakeMainUsers.setSelected(false);
            btnChangesUser.setDisable(true);
            btnResetUser.setDisable(true);
            TxtNameInput.setDisable(true);
            TxtSurnameInput.setDisable(true);
            TxtNumberInput.setDisable(true);
            TxtEmailInput.setDisable(true);
        }
    }

    private void setUpDataPaths() {
        if(SettingsFileController.getImportOnStartUp()){
            RBtnImportStartUpYes.setSelected(true);
            RBtnImportStartUpNo.setSelected(false);
        }else{
            RBtnImportStartUpYes.setSelected(false);
            RBtnImportStartUpNo.setSelected(true);
            TxtImportPath.setDisable(true);
        }
        TxtImportPath.setText(SettingsFileController.getImportPath());
        TxtImportPath.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                changeListener.replace("PathsTab.Data.Import",TxtImportPath.getText());
            }
        });
        TxtExportPath.setText(SettingsFileController.getExportPath());
        TxtExportPath.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                changeListener.replace("PathsTab.Data.Export",TxtExportPath.getText());
            }
        });
    }

    private void setUpDataPDF() {
        List<String> options = new ArrayList<>();
        options.add("Do not add");
        options.add("1");
        options.add("2");
        options.add("3");
        CbxQuotationInfoPosition.getItems().addAll(options);
        CbxQuotationTablePosition.getItems().addAll(options);
        CbxQuotationTextPosition.getItems().addAll(options);

        CbxCostSheetInfoPosition.getItems().addAll(options);
        CbxCostSheetTablePosition.getItems().addAll(options);
        CbxCostSheetTextPosition.getItems().addAll(options);

        CbxCheckingSheetInfoPosition.getItems().addAll(options);
        CbxCheckingSheetTablePosition.getItems().addAll(options);
        CbxCheckingSheetTextPosition.getItems().addAll(options);

        Map<String, Integer> quotationPosition =SettingsFileController.getQuotationPositions();
        CbxQuotationInfoPosition.setValue(positionsIntegerToString(quotationPosition.get("PDFTab.Data.Quotation.Position.info")));
        CbxQuotationTablePosition.setValue(positionsIntegerToString(quotationPosition.get("PDFTab.Data.Quotation.Position.table")));
        CbxQuotationTextPosition.setValue(positionsIntegerToString(quotationPosition.get("PDFTab.Data.Quotation.Position.text")));
        TxtAreaQuotationTextInput.setText(SettingsFileController.getQuotationText());
        if(quotationPosition.get("PDFTab.Data.Quotation.Position.table")==-1)
            disableEnableQuotation(true);
        CbxQuotationTablePosition.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue&&positionsStringToInteger(CbxQuotationTablePosition.getSelectionModel().getSelectedItem())==-1){
                disableEnableQuotation(true);
            }
        });
        TxtAreaQuotationTextInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue&&!TxtAreaQuotationTextInput.isDisable()){
                changeListener.replace("PDFTab.Data.Quotation.Text",TxtAreaQuotationTextInput.getText());
            }
        });

        Map<String, Integer> costSheetPosition =SettingsFileController.getCostingPositions();
        CbxCostSheetInfoPosition.setValue(positionsIntegerToString(costSheetPosition.get("PDFTab.Data.CostingSheet.Position.info")));
        CbxCostSheetTablePosition.setValue(positionsIntegerToString(costSheetPosition.get("PDFTab.Data.CostingSheet.Position.table")));
        CbxCostSheetTextPosition.setValue(positionsIntegerToString(costSheetPosition.get("PDFTab.Data.CostingSheet.Position.text")));
        TxtAreaCostSheetTextInput.setText(SettingsFileController.getCostingText());
        if(costSheetPosition.get("PDFTab.Data.CostingSheet.Position.table")==-1)
            disableEnableCosting(true);
        CbxCostSheetTablePosition.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue&&positionsStringToInteger(CbxCostSheetTablePosition.getSelectionModel().getSelectedItem())==-1){
                disableEnableCosting(true);
            }
        });
        TxtAreaCostSheetTextInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue&&!TxtAreaCostSheetTextInput.isDisable()){
                changeListener.replace("PDFTab.Data.CostingSheet.Text",TxtAreaCostSheetTextInput.getText());
            }
        });

        Map<String, Integer> checkingSheetPosition =SettingsFileController.getCheckingPositions();
        CbxCheckingSheetInfoPosition.setValue(positionsIntegerToString(checkingSheetPosition.get("PDFTab.Data.CheckingSheet.Position.info")));
        CbxCheckingSheetTablePosition.setValue(positionsIntegerToString(checkingSheetPosition.get("PDFTab.Data.CheckingSheet.Position.table")));
        CbxCheckingSheetTextPosition.setValue(positionsIntegerToString(checkingSheetPosition.get("PDFTab.Data.CheckingSheet.Position.text")));
        TxtAreaCheckingSheetTextInput.setText(SettingsFileController.getCheckingSheetText());
        if(checkingSheetPosition.get("PDFTab.Data.CheckingSheet.Position.table")==-1)
            disableEnableChecking(true);
        CbxCheckingSheetTablePosition.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue&&positionsStringToInteger(CbxCheckingSheetTablePosition.getSelectionModel().getSelectedItem())==-1){
                disableEnableCosting(true);
            }
        });
        TxtAreaCheckingSheetTextInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue&&!TxtAreaCheckingSheetTextInput.isDisable()){
                changeListener.replace("PDFTab.Data.CheckingSheet.Text",TxtAreaCheckingSheetTextInput.getText());
            }
        });
    }

    private String positionsIntegerToString(int position){
        return (position==-1)?"Do not add":Integer.toString(position);
    }

    private Integer positionsStringToInteger(String value) {
        if(value.equals("1")){
            return 1;
        }else if(value.equals("2")){
            return 2;
        }else if(value.equals("3")){
            return 3;
        }else if(value.equals("4")){
            return 4;
        }else{
            return -1;
        }
    }

    public void setUpEnableOrDisableTabs(){
        if(SettingsFileController.isUsersTabEnable())
            enableUsersTab();
        else
            disableUsersTab();
        if(SettingsFileController.isPathsTabEnable())
            enablePathsTab();
        else
            disablePathsTab();
       if(SettingsFileController.isPDFTadEnable())
            enablePDFTab();
       else
            disablePDFTab();
    }

    private void enableUsersTab() {
        if(SettingsFileController.isUsersTabMakeMainOnlyEnable()){
            disableUsersTab();
            LVUsersList.setDisable(false);
            CheckMakeMainUsers.setDisable(false);
        }else{
            LVUsersList.setDisable(false);
            TxtNameInput.setDisable(false);
            TxtSurnameInput.setDisable(false);
            TxtNumberInput.setDisable(false);
            TxtEmailInput.setDisable(false);
            CheckMakeMainUsers.setDisable(false);
            btnChangesUser.setDisable(true);
        }
    }

    private void disableUsersTab() {
        LVUsersList.setDisable(true);
        TxtNameInput.setDisable(true);
        TxtSurnameInput.setDisable(true);
        TxtNumberInput.setDisable(true);
        TxtEmailInput.setDisable(true);
        CheckMakeMainUsers.setDisable(true);
        btnChangesUser.setDisable(true);
    }

    private void enablePathsTab() {
        RBtnImportStartUpYes.setDisable(!SettingsFileController.isPathsImportEnable());
        RBtnImportStartUpNo.setDisable(!SettingsFileController.isPathsImportEnable());
        TxtImportPath.setDisable(!SettingsFileController.isPathsImportEnable()&&SettingsFileController.getImportOnStartUp());
        btnImportPath.setDisable(!SettingsFileController.isPathsImportEnable()&&SettingsFileController.getImportOnStartUp());
        TxtExportPath.setDisable(!SettingsFileController.isPathsExportEnable());
        btnExportPath.setDisable(!SettingsFileController.isPathsExportEnable());
    }

    private void disablePathsTab() {
        RBtnImportStartUpYes.setDisable(true);
        RBtnImportStartUpNo.setDisable(true);
        btnImportPath.setDisable(true);
        TxtImportPath.setDisable(true);
        btnExportPath.setDisable(true);
        TxtExportPath.setDisable(true);
    }

    private void enablePDFTab() {
        disableEnableQuotation(false);
        disableEnableCosting(false);
        disableEnableChecking(false);
    }

    private void disablePDFTab() {
        disableEnableQuotation(true);
        disableEnableCosting(true);
        disableEnableChecking(true);
    }

    private void disableEnableCosting(boolean disable){
        CbxCostSheetInfoPosition.setDisable(disable);
        CbxCostSheetTablePosition.setDisable(disable);
        CbxCostSheetTextPosition.setDisable(disable);
        TxtAreaCostSheetTextInput.setDisable((disable)?true:SettingsFileController.hasCostingText());
    }

    private void disableEnableQuotation(boolean disable){
        CbxQuotationInfoPosition.setDisable(disable);
        CbxQuotationTablePosition.setDisable(disable);
        CbxQuotationTextPosition.setDisable(disable);
        TxtAreaQuotationTextInput.setDisable((disable)?true:SettingsFileController.hasQuotationText());
    }

    private void disableEnableChecking(boolean disable){
        CbxCheckingSheetInfoPosition.setDisable(disable);
        CbxCheckingSheetTablePosition.setDisable(disable);
        CbxCheckingSheetTextPosition.setDisable(disable);
        TxtAreaCheckingSheetTextInput.setDisable((disable)?true:SettingsFileController.hasCheckingText());
    }

    private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void enableDisableApplyButton(boolean newValue) {
        btnApply.setDisable(newValue);
    }

}
