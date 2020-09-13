package sample.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sample.dataReader.Item;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Tab1Controller implements Initializable {

    @FXML
    private TextField TxtSearch;

    @FXML
    private Button BtnAddToInvoice;

    @FXML
    private TableView<Item> TVItemsTable;

    @FXML
    private TableColumn<Item, String> colStockCode;

    @FXML
    private TableColumn<Item, String> colDescription;

    @FXML
    private TableColumn<Item, String> colQuantity;

    @FXML
    private TableColumn<Item, String> colUnit;

    @FXML
    private TableColumn<Item, String> colPrice;

    @FXML
    private TableColumn<Item, String> colTotal;

    private ObservableList<Item> itemData;

    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTalbe();
    }

    @FXML
    private void moveItemToInvoice(ActionEvent event) {
        List<Item> items =TVItemsTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
        List<Double> quantity = new ArrayList<>();
        for (Item item:items) {
            quantity.add(1.0);
            if(item.getCostQuantityDouble()==1){
                remove(item);
            }
        }
        mainController.addToInvoice(items,quantity);
    }

    public void add(Item newItem) {
        if(!itemData.contains(newItem)){
            itemData.add(newItem);
            if(TxtSearch.isDisable()||BtnAddToInvoice.isDisable()){
                TxtSearch.setDisable(false);
                BtnAddToInvoice.setDisable(false);
            }
        }
    }
    public void remove(Item selectedItem){
        itemData.remove(selectedItem);
        if(itemData.size()==0){
            TxtSearch.setDisable(true);
            BtnAddToInvoice.setDisable(true);
        }
    }

    private void initializeTalbe() {
        itemData = FXCollections.observableArrayList();

        colStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().costQuantityProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().costPriceProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalCostPriceProperty());

        FilteredList<Item> filteredData = new FilteredList<>(itemData, p -> true);
        TxtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getStockCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getUnit().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getCostQuantity().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getCostQuantity().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getTotalCostPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getCostPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Item> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(TVItemsTable.comparatorProperty());

        TVItemsTable.setItems(sortedData);
        TVItemsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        add(new Item("R1", "RAM", "none", 10, 500, 1000));
        add(new Item("C1", "CPU", "none", 5, 2500, 5000));
        add(new Item("F1", "Fan", "none", 100, 40, 50));
        add(new Item("S1", "SDD", "none", 10, 1500, 2000));
        add(new Item("H1", "HDD", "none", 15, 400, 700));
        add(new Item("M1", "Mother braod", "none", 5, 3500, 5499.99));
        add(new Item("G1", "Graphic card", "none", 8, 7500, 10000));
        add(new Item("C2", "Cades", "meters", 100, 10, 20));
        add(new Item("C3", "Cadles", "none", 4, 5000.54, 7499.99));
    }


    public void setMainController(MainController mainController) {
        this.mainController=mainController;
    }
}
