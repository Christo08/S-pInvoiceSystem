package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private Tab UsersTab;

    @FXML
    private ListView<?> LVUsersList;

    @FXML
    private TextField TxtNameInput;

    @FXML
    private TextField TxtSurnameInput;

    @FXML
    private TextField TxtNumberInput;

    @FXML
    private TextField TxtEmailInput;

    @FXML
    private Button BtnAddUsers;

    @FXML
    private Button BtnChangesUser;

    @FXML
    private Button BtnRemoveUser;

    @FXML
    private CheckBox CheckMakeMainUsers;

    @FXML
    private Tab PDFTab;

    @FXML
    private ComboBox<?> CbxUserInfoPosition;

    @FXML
    private ComboBox<?> CbxCostingSheetPosition;

    @FXML
    private ComboBox<?> CbxQuotationPosition;

    @FXML
    private ComboBox<?> CbxTextPosition;

    @FXML
    private TextArea TxtAreaTextInput;

    @FXML
    private Tab PathsTab;

    @FXML
    private RadioButton RBtnImportStartUpYes;

    @FXML
    private RadioButton RBtnImportStartUpNo;

    @FXML
    private TextField TxtImportURL;

    @FXML
    private TextField TxtExportURL;

    @FXML
    void addNewUser(ActionEvent event) {

    }

    @FXML
    void changesUsers(ActionEvent event) {

    }

    @FXML
    void disableImportPathTextField(ActionEvent event) {

    }

    @FXML
    void enableImportPathTextField(ActionEvent event) {

    }

    @FXML
    void populateData(MouseEvent event) {

    }

    @FXML
    void removeUser(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
