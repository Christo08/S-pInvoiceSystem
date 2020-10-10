package sample.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.MainController;
import sample.data.User;

import java.net.URL;
import java.util.*;

public class SettingsController implements Initializable {

    @FXML
    private BorderPane BorderPaneSettings;

    @FXML
    private Button BtnAddUsers;

    @FXML
    private Button BtnApply;

    @FXML
    private Button BtnChangesUser;

    @FXML
    private Button BtnRemoveUser;

    @FXML
    private Button BtnResetUser;

    @FXML
    private ComboBox<String> CbxUserInfoPosition;

    @FXML
    private ComboBox<String> CbxCostingSheetPosition;

    @FXML
    private ComboBox<String> CbxQuotationPosition;

    @FXML
    private ComboBox<String> CbxTextPosition;

    @FXML
    private CheckBox CheckMakeMainUsers;

    @FXML
    private ListView<User> LVUsersList;

    @FXML
    private Tab PDFTab;

    @FXML
    private Tab PathsTab;

    @FXML
    private Tab UsersTab;

    @FXML
    private TextArea TxtAreaTextInput;

    @FXML
    private TextField TxtExportURL;

    @FXML
    private TextField TxtEmailInput;

    @FXML
    private TextField TxtImportURL;

    @FXML
    private TextField TxtSurnameInput;

    @FXML
    private TextField TxtNameInput;

    @FXML
    private TextField TxtNumberInput;

    @FXML
    private RadioButton RBtnImportStartUpYes;

    @FXML
    private RadioButton RBtnImportStartUpNo;

    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final FileChooser fileChooser = new FileChooser();

    private MainController mainController;
    private SettingsFileController settingsFileController;
    private ObservableList<String> positions;

    private Map<String,Boolean> changes;
    private Stage primaryStage;
    private ObservableList<User>  listUser;
    private User loadedUser;

    public SettingsController() {
        changes=new HashMap<>();
        changes.put("AddedUser",false);
        changes.put("RemoveUser",false);
        changes.put("ChangeUserName",false);
        changes.put("ChangeUserSurname",false);
        changes.put("ChangeUserNumber",false);
        changes.put("ChangeUserEmail",false);
        changes.put("ChangeMainUser",false);
        changes.put("ChangePDFPosition",false);
        changes.put("ChangePDFText",false);
        changes.put("ChangePathsOnStartUp",false);
        changes.put("ChangePathsImportPath",false);
        changes.put("ChangePathsExportPath",false);

        List<String> list = new ArrayList<>();
        positions = FXCollections.observableList(list);
        positions.add("1");
        positions.add("2");
        positions.add("3");
        positions.add("4");
        positions.add("None");
    }

    @FXML
    void applyChanges(ActionEvent event) {
        if(checkForChanges()){
            if(changes.get("ChangePDFText")){
                settingsFileController.setPDFText(TxtAreaTextInput.getText());
            }
            if(changes.get("ChangePDFPosition")){
                List<Integer> positions =new ArrayList();
                positions.add(positionsToString(CbxUserInfoPosition.getValue()));
                positions.add(positionsToString(CbxCostingSheetPosition.getValue()));
                positions.add(positionsToString(CbxQuotationPosition.getValue()));
                positions.add(positionsToString(CbxTextPosition.getValue()));
                settingsFileController.setPDFPositions(positions);
            }
            if(changes.get("ChangePathsOnStartUp")){
                settingsFileController.setImportOnStartUp(RBtnImportStartUpYes.isSelected());
            }
            if(changes.get("ChangePathsImportPath")){
                settingsFileController.setImportPath(TxtImportURL.getText());
            }
            if(changes.get("ChangePathsExportPath")){
                settingsFileController.setImportPath(TxtExportURL.getText());
            }
            if(changes.get("AddedUser")){
                settingsFileController.addUser(new ArrayList<>(listUser));
            }
            if(changes.get("RemoveUser")){
                settingsFileController.removeUser(new ArrayList<>(listUser));
            }
            if(changes.get("ChangeUserSurname")||changes.get("ChangeUserName")||changes.get("ChangeUserNumber")||changes.get("ChangeUserEmail")||changes.get("ChangeMainUser")){
                settingsFileController.changesUser(new ArrayList<>(listUser));
            }
            settingsFileController.applyChanges();
            BtnApply.setDisable(true);
        }
    }

    private Integer positionsToString(String value) {
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

    @FXML
    void closePopUp(ActionEvent event) {
        closeStage(event);
    }

    @FXML
    void addNewUser(ActionEvent event) {
        User newUser = new User(TxtNameInput.getText(),TxtSurnameInput.getText(),TxtNumberInput.getText(),TxtEmailInput.getText(),CheckMakeMainUsers.isSelected());
        for(User oldUser: listUser){
            if(oldUser.getName().equals(newUser.getName())&&oldUser.getSurname().equals(newUser.getSurname())){
                System.out.println("error");
                return;
            }
        }
        loadedUser=newUser;
        listUser.add(newUser);
        BtnApply.setDisable(!checkForChanges());
        changes.put("AddedUser",true);
    }

    @FXML
    void changesUsers(ActionEvent event) {
        if(loadedUser!=null&&checkForUserChanges()){
            loadedUser.setName(TxtNameInput.getText());
            loadedUser.setSurname(TxtSurnameInput.getText());
            loadedUser.setNumber(TxtNumberInput.getText());
            loadedUser.setEmail(TxtEmailInput.getText());
            loadedUser.setMainUser(settingsFileController.isUsersTabChangesButtonEnable());
            if(loadedUser.isMainUser()==true){}
            int counter=0;
            for (User oldUser:listUser) {
                if(oldUser.getId().equals(loadedUser.getId()))
                    break;
                counter++;
            }
            listUser.set(counter,loadedUser);
            BtnApply.setDisable(!checkForChanges());
        }
    }

    @FXML
    void disableImportPathTextField(ActionEvent event) {

    }

    @FXML
    void enableImportPathTextField(ActionEvent event) {

    }

    @FXML
    void populateData(MouseEvent event) {
        System.out.println(LVUsersList.getSelectionModel().getSelectedItem());
        if(loadedUser!=LVUsersList.getSelectionModel().getSelectedItem()) {
            loadedUser=LVUsersList.getSelectionModel().getSelectedItem();
            TxtNameInput.setText(loadedUser.getName());
            TxtSurnameInput.setText(loadedUser.getSurname());
            TxtEmailInput.setText(loadedUser.getEmail());
            TxtNumberInput.setText(loadedUser.getNumber());
            CheckMakeMainUsers.setSelected(loadedUser.isMainUser());
            if(settingsFileController.isUsersTabRemoveButtonEnable())
                BtnRemoveUser.setDisable(false);
            BtnResetUser.setDisable(false);
        }
    }

    @FXML
    void removeUser(ActionEvent event) {
        if(loadedUser!=null){
            listUser.remove(loadedUser);
            changes.replace("RemoveUser",true);
            TxtNameInput.setText("");
            TxtSurnameInput.setText("");
            TxtNumberInput.setText("");
            TxtEmailInput.setText("");
            CheckMakeMainUsers.setSelected(false);
            TxtNameInput.setStyle("");
            TxtSurnameInput.setStyle("");
            TxtNumberInput.setStyle("");
            TxtEmailInput.setStyle("");
            BtnApply.setDisable(true);
        }
    }

    @FXML
    void resetUser(ActionEvent event){
        changes.put("AddedUser",false);
        changes.put("RemoveUser",false);
        changes.put("ChangeUserName",false);
        changes.put("ChangeUserSurname",false);
        changes.put("ChangeUserNumber",false);
        changes.put("ChangeUserEmail",false);
        changes.put("ChangeMainUser",false);

        TxtNameInput.setText("");
        TxtSurnameInput.setText("");
        TxtNumberInput.setText("");
        TxtEmailInput.setText("");
        CheckMakeMainUsers.setSelected(false);

        TxtNameInput.setStyle("");
        TxtSurnameInput.setStyle("");
        TxtNumberInput.setStyle("");
        TxtEmailInput.setStyle("");

        BtnAddUsers.setDisable(true);
        BtnChangesUser.setDisable(true);
        BtnRemoveUser.setDisable(true);
        BtnApply.setDisable(!checkForChanges());

        listUser = FXCollections.observableArrayList(settingsFileController.getAllUsers());
        LVUsersList.setItems(listUser);

        for(User user:listUser){
            if(user.isMainUser()){
                LVUsersList.scrollTo(user);
                LVUsersList.getSelectionModel().select(user);
                TxtNameInput.setText(user.getName());
                TxtSurnameInput.setText(user.getSurname());
                TxtNumberInput.setText(user.getNumber());
                TxtEmailInput.setText(user.getEmail());
                CheckMakeMainUsers.setSelected(user.isMainUser());
                break;
            }
        }
    }

    @FXML
    void errorChecking(ActionEvent event) {
        ComboBox<String> comboBox=(ComboBox<String>) event.getSource();
        if(CbxUserInfoPosition.getValue().equals(comboBox.getValue())&&!comboBox.getValue().equals("None")&&!CbxUserInfoPosition.getId().equals(comboBox.getId())){
            CbxUserInfoPosition.setStyle("-fx-border-color: red");
            comboBox.setStyle("-fx-border-color: red");
        }
        else if(CbxCostingSheetPosition.getValue().equals(comboBox.getValue())&&!comboBox.getValue().equals("None")&&!CbxCostingSheetPosition.getId().equals(comboBox.getId())){
            CbxCostingSheetPosition.setStyle("-fx-border-color: red");
            comboBox.setStyle("-fx-border-color: red");
        }
        else if(CbxQuotationPosition.getValue().equals(comboBox.getValue())&&!comboBox.getValue().equals("None")&&!CbxQuotationPosition.getId().equals(comboBox.getId())){
            CbxQuotationPosition.setStyle("-fx-border-color: red");
            comboBox.setStyle("-fx-border-color: red");
        }
        else if(CbxTextPosition.getValue().equals(comboBox.getValue())&&!comboBox.getValue().equals("None")&&!CbxTextPosition.getId().equals(comboBox.getId())){
            CbxTextPosition.setStyle("-fx-border-color: red");
            comboBox.setStyle("-fx-border-color: red");
        }
        else{
            CbxUserInfoPosition.setStyle("");
            CbxCostingSheetPosition.setStyle("");
            CbxQuotationPosition.setStyle("");
            CbxTextPosition.setStyle("");
            List<String> newValues= new ArrayList<>();
            newValues.add(CbxUserInfoPosition.getValue());
            newValues.add(CbxCostingSheetPosition.getValue());
            newValues.add(CbxQuotationPosition.getValue());
            newValues.add(CbxTextPosition.getValue());
            List<Integer> newIntValues = new ArrayList<>();
            for (String value: newValues) {
                if(value.equals("None")){
                    newIntValues.add(-1);
                }else{
                    newIntValues.add(Integer.parseInt(value));
                }
            }

            changes.replace("ChangePDFPosition",false);
            boolean hasChanged =CbxUserInfoPosition.getValue().equals("1")&&CbxCostingSheetPosition.getValue().equals("2")&&CbxQuotationPosition.getValue().equals("3")&&CbxTextPosition.getValue().equals("4");
            if(!checkForChanges()){
                BtnApply.setDisable(hasChanged);
            }
            changes.replace("ChangePDFPosition",!hasChanged);

            if(CbxTextPosition.getValue().equals("None")){
                TxtAreaTextInput.setDisable(true);
            }else{
                TxtAreaTextInput.setDisable(false);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMainController(MainController mainController) {
        this.mainController=mainController;
        settingsFileController = new SettingsFileController();
        setUpEnableOrDisableTabs();
        setUpDataInTabs();
    }

    public void showSettings() {
        BorderPaneSettings.setVisible(true);
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private boolean checkForChanges() {
        for (boolean changes: changes.values()) {
            if(changes)
                return true;
        }
        return false;
    }

    private boolean checkForUserChanges(){
        return changes.get("ChangeUserName")||
        changes.get("ChangeUserSurname")||
        changes.get("ChangeUserNumber")||
        changes.get("ChangeUserEmail")||
        changes.get("ChangeMainUser");
    }

    private void setUpDataInTabs() {
        setUpDataPDF();
        setUpDataPaths();
        setUpDataUsers();
    }

    private void setUpDataUsers() {
        LVUsersList.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName()== null||item.getSurname()==null) {
                    setText(null);
                } else {
                    setText((item.getName()+" "+item.getSurname()));
                }
            }
        });
        listUser = FXCollections.observableArrayList(settingsFileController.getAllUsers());
        LVUsersList.setItems(listUser);
        TxtNameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty()&&loadedUser!=null&&!newValue.isEmpty()){
                changes.replace("ChangeUserName",false);
                boolean hasChanged =newValue.equals(loadedUser.getName());
                if(!checkForChanges()){
                    BtnApply.setDisable(hasChanged);
                }
                if(settingsFileController.isUsersTabChangesButtonEnable())
                    BtnChangesUser.setDisable(hasChanged);
                changes.replace("ChangeUserName",!hasChanged);
            }
            if(newValue.isEmpty()){
                TxtNameInput.setStyle("-fx-border-color: red");
                BtnAddUsers.setDisable(true);
                BtnChangesUser.setDisable(true);
            }else{
                TxtNameInput.setStyle("");
                BtnAddUsers.setDisable(!settingsFileController.isUsersTabAddButtonEnable());
                BtnChangesUser.setDisable(!settingsFileController.isUsersTabChangesButtonEnable());
            }
            BtnResetUser.setDisable(false);
        });
        TxtSurnameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty()&&loadedUser!=null&&newValue.isEmpty()){
                changes.replace("ChangeUserSurname",false);
                boolean hasChanged =newValue.equals(loadedUser.getName());
                if(!checkForChanges()){
                    BtnApply.setDisable(hasChanged);
                }
                if(settingsFileController.isUsersTabChangesButtonEnable())
                    BtnChangesUser.setDisable(hasChanged);
                changes.replace("ChangeUserSurname",!hasChanged);
            }
            if(newValue.isEmpty()){
                TxtSurnameInput.setStyle("-fx-border-color: red");
                BtnAddUsers.setDisable(true);
                BtnChangesUser.setDisable(true);
            }else{
                TxtSurnameInput.setStyle("");
                BtnAddUsers.setDisable(!settingsFileController.isUsersTabAddButtonEnable());
                BtnChangesUser.setDisable(!settingsFileController.isUsersTabChangesButtonEnable());
            }
            BtnResetUser.setDisable(false);
        });
        TxtNumberInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String pattern = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
            if (newValue.matches(pattern)) {
                if (!oldValue.isEmpty()&&loadedUser!=null){
                    changes.replace("ChangeUserSurname",false);
                    boolean hasChanged =newValue.equals(loadedUser.getNumber());
                    if(!checkForChanges()){
                        BtnApply.setDisable(hasChanged);
                    }
                    if(settingsFileController.isUsersTabChangesButtonEnable())
                        BtnChangesUser.setDisable(hasChanged);
                    changes.replace("ChangeUserSurname",!hasChanged);
                }
                TxtNumberInput.setStyle("");
                BtnResetUser.setDisable(false);
                BtnAddUsers.setDisable(!settingsFileController.isUsersTabAddButtonEnable());
                BtnChangesUser.setDisable(!settingsFileController.isUsersTabChangesButtonEnable());
            } else {
                TxtNumberInput.setStyle("-fx-border-color: red");
                BtnAddUsers.setDisable(true);
                BtnChangesUser.setDisable(true);
            }
        });
        TxtEmailInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String pattern = "^(.+)@(.+)$";
            if (newValue.matches(pattern)) {
                if (!oldValue.isEmpty()&&loadedUser!=null){
                    changes.replace("ChangeUserEmail",false);
                    boolean hasChanged =newValue.equals(loadedUser.getEmail());
                    if(!checkForChanges()){
                        BtnApply.setDisable(hasChanged);
                    }
                    if(settingsFileController.isUsersTabChangesButtonEnable())
                        BtnChangesUser.setDisable(hasChanged);
                    changes.replace("ChangeUserEmail",!hasChanged);
                }
                TxtEmailInput.setStyle("");
                BtnResetUser.setDisable(false);
                BtnAddUsers.setDisable(!settingsFileController.isUsersTabAddButtonEnable());
                BtnChangesUser.setDisable(!settingsFileController.isUsersTabChangesButtonEnable());
            } else {
                TxtEmailInput.setStyle("-fx-border-color: red");
                BtnAddUsers.setDisable(true);
                BtnChangesUser.setDisable(true);
            }
        });
        CheckMakeMainUsers.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (loadedUser!=null){
                changes.replace("ChangeMainUser",false);
                boolean hasChanged =newValue==loadedUser.isMainUser();
                if(!checkForChanges()){
                    BtnApply.setDisable(hasChanged);
                }
                if(settingsFileController.isUsersTabChangesButtonEnable())
                    BtnChangesUser.setDisable(hasChanged);
                for(User user:listUser){
                    user.setMainUser(false);
                }
                changes.replace("ChangeMainUser",!hasChanged);
            }
            BtnResetUser.setDisable(false);
        });
        for(User user:listUser){
            if(user.isMainUser()){
                Platform.runLater(() -> {
                    LVUsersList.scrollTo(user);
                    LVUsersList.getSelectionModel().select(user);
                    TxtNameInput.setText(user.getName());
                    TxtSurnameInput.setText(user.getSurname());
                    TxtNumberInput.setText(user.getNumber());
                    TxtEmailInput.setText(user.getEmail());
                    CheckMakeMainUsers.setSelected(user.isMainUser());
                });
                break;
            }
        }
    }

    private void setUpDataPaths() {
        if(settingsFileController.getImportOnStartUp()){
            RBtnImportStartUpYes.setSelected(true);
            RBtnImportStartUpNo.setSelected(false);
        }else{
            RBtnImportStartUpYes.setSelected(false);
            RBtnImportStartUpNo.setSelected(true);
        }
        RBtnImportStartUpYes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            changes.replace("ChangePathsOnStartUp",false);
            boolean hasChanged =newValue&& settingsFileController.getImportOnStartUp();
            if(!checkForChanges()){
                BtnApply.setDisable(hasChanged);
            }
            changes.replace("ChangePathsOnStartUp",!hasChanged);
            TxtImportURL.setDisable(false);
        });
        RBtnImportStartUpNo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            changes.replace("ChangePathsOnStartUp",false);
            boolean hasChanged =(newValue)&& (!settingsFileController.getImportOnStartUp());
            if(!checkForChanges()){
                BtnApply.setDisable(hasChanged);
            }
            changes.replace("ChangePathsOnStartUp",!hasChanged);
            TxtImportURL.setDisable(true);
        });
        TxtImportURL.setText(settingsFileController.getImportPath());
        TxtImportURL.textProperty().addListener((observable, oldValue, newValue) -> {
            changes.replace("ChangePathsImportPath",false);
            boolean hasChanged =newValue.equals(settingsFileController.getImportPath());
            if(!checkForChanges()){
                BtnApply.setDisable(hasChanged);
            }
            changes.replace("ChangePathsImportPath",!hasChanged);
        });
        TxtExportURL.setText(settingsFileController.getExportPath());
        TxtExportURL.textProperty().addListener((observable, oldValue, newValue) -> {
            changes.replace("ChangePathsExportPath",false);
            boolean hasChanged =newValue.equals(settingsFileController.getExportPath());
            if(!checkForChanges()){
                BtnApply.setDisable(hasChanged);
            }
            changes.replace("ChangePathsExportPath",!hasChanged);
        });
    }

    private void setUpDataPDF() {
        CbxUserInfoPosition.setItems(positions);
        CbxCostingSheetPosition.setItems(positions);
        CbxQuotationPosition.setItems(positions);
        CbxTextPosition.setItems(positions);
        CbxUserInfoPosition.getSelectionModel().select(0);
        CbxCostingSheetPosition.getSelectionModel().select(1);
        CbxQuotationPosition.getSelectionModel().select(2);
        CbxTextPosition.getSelectionModel().select(3);
        TxtAreaTextInput.setText(settingsFileController.getPDFText());
        TxtAreaTextInput.textProperty().addListener((observable, oldValue, newValue) ->{
            if(settingsFileController.getPDFText().equals(newValue)){
                BtnApply.setDisable(true);
            }else{
                BtnApply.setDisable(false);
            }
            changes.replace("ChangePDFText",false);
            boolean hasChanged =settingsFileController.getPDFText().equals(newValue);
            if(!checkForChanges()){
                BtnApply.setDisable(hasChanged);
            }
            changes.replace("ChangePDFText",!hasChanged);
        });
    }

    public void setUpEnableOrDisableTabs(){
        if(settingsFileController.isUsersTabEnable())
            enableUsersTab();
        else
            disableUsersTab();
        if(settingsFileController.isPathsTabEnable())
            enablePathsTab();
        else
            disablePathsTab();
       if(settingsFileController.isPDFTabEnable())
            enablePDFTab();
       else
            disablePDFTab();
    }

    private void enableUsersTab() {
        UsersTab.setDisable(false);
        LVUsersList.setDisable(false);
        if(settingsFileController.isUsersTabMakeMainOnlyEnable()){
            TxtNameInput.setDisable(true);
            TxtSurnameInput.setDisable(true);
            TxtNumberInput.setDisable(true);
            TxtEmailInput.setDisable(true);
            CheckMakeMainUsers.setDisable(false);
            BtnAddUsers.setDisable(true);
            BtnChangesUser.setDisable(true);
            BtnRemoveUser.setDisable(true);
        }else{
            CheckMakeMainUsers.setDisable(false);
            BtnAddUsers.setDisable(true);
            BtnChangesUser.setDisable(true);
            BtnRemoveUser.setDisable(true);
            if(!settingsFileController.isUsersTabAddButtonEnable()&&!settingsFileController.isUsersTabChangesButtonEnable()&&!settingsFileController.isUsersTabRemoveButtonEnable()){
                TxtNameInput.setDisable(true);
                TxtSurnameInput.setDisable(true);
                TxtNumberInput.setDisable(true);
                TxtEmailInput.setDisable(true);
            }else{
                TxtNameInput.setDisable(false);
                TxtSurnameInput.setDisable(false);
                TxtNumberInput.setDisable(false);
                TxtEmailInput.setDisable(false);
            }
        }
    }

    private void disableUsersTab() {
        UsersTab.setDisable(true);
        LVUsersList.setDisable(true);
        TxtNameInput.setDisable(true);
        TxtSurnameInput.setDisable(true);
        TxtNumberInput.setDisable(true);
        TxtEmailInput.setDisable(true);
        CheckMakeMainUsers.setDisable(true);
        BtnAddUsers.setDisable(true);
        BtnChangesUser.setDisable(true);
        BtnRemoveUser.setDisable(true);
    }

    private void enablePathsTab() {
        PathsTab.setDisable(false);
        if(settingsFileController.isPathsImportEnable()){
            RBtnImportStartUpYes.setDisable(false);
            RBtnImportStartUpNo.setDisable(false);
            if(settingsFileController.getImportOnStartUp()) {
                TxtImportURL.setDisable(false);
            }else{
                TxtImportURL.setDisable(true);
            }
        }else{
            RBtnImportStartUpYes.setDisable(true);
            RBtnImportStartUpNo.setDisable(true);
            TxtImportURL.setDisable(true);

        }
        TxtExportURL.setDisable(!settingsFileController.isPathsExportEnable());

    }

    private void disablePathsTab() {
        PathsTab.setDisable(true);
        RBtnImportStartUpYes.setDisable(true);
        RBtnImportStartUpNo.setDisable(true);
        TxtImportURL.setDisable(true);
        TxtExportURL.setDisable(true);
    }

    private void enablePDFTab() {
        PDFTab.setDisable(false);
        CbxUserInfoPosition.setDisable(false);
        CbxCostingSheetPosition.setDisable(false);
        CbxQuotationPosition.setDisable(false);
        CbxTextPosition.setDisable(!settingsFileController.isPDFTextEnable());
        TxtAreaTextInput.setDisable(!settingsFileController.isPDFTextEnable());
    }

    private void disablePDFTab() {
        PDFTab.setDisable(true);
        CbxUserInfoPosition.setDisable(true);
        CbxCostingSheetPosition.setDisable(true);
        CbxQuotationPosition.setDisable(true);
        CbxTextPosition.setDisable(true);
        TxtAreaTextInput.setDisable(true);
    }

    public SettingsFileController getSettingsFileController() {
        return settingsFileController;
    }

    public Node getMainNode() {
        return BorderPaneSettings;
    }

    private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
