package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import sample.dataReader.Item;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoriesController implements Initializable {

    @FXML
    private TextField TxtSearch;

    @FXML
    private Button BtnAddToInvoice;

    @FXML
    private Button BtnResetItems;

    @FXML
    private TabPane Tables;

    private MainController mainController;
    private Category categoryA;
    private Category categoryB;
    private Category categoryC;

    private ObservableList<Item> allDataItem;
    private FilteredList<Item> categoryCFilteredData;

    public CategoriesController() {
    }

    public void setMainController(MainController mainController) {
        this.mainController=mainController;
    }

    @FXML
    private void moveItemToInvoice(ActionEvent event) {
    }

    @FXML
    private void resetItems(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryA= new Category();
        categoryB= new Category();
        categoryC= new Category();
        Tables.getTabs().add(categoryA);
        Tables.getTabs().add(categoryB);
        Tables.getTabs().add(categoryC);
    }
}
