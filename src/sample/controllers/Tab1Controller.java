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
    private TableColumn<Item, String> colUnit;

    @FXML
    private TableColumn<Item, String> colProfitPercent;

    @FXML
    private TableColumn<Item, String> colCostPrice;

    @FXML
    private TableColumn<Item, String> colSellingPrice;

    private ObservableList<Item> itemData;

    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTalbe();
    }

    @FXML
    private void moveItemToInvoice(ActionEvent event) {
        List<Item> items =TVItemsTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
        List<Integer> quantity = new ArrayList<>();
        for (Item item:items) {
            quantity.add(1);
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
        colProfitPercent.setCellValueFactory(cellData -> cellData.getValue().profitPercentProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colCostPrice.setCellValueFactory(cellData -> cellData.getValue().costPriceProperty());
        colSellingPrice.setCellValueFactory(cellData -> cellData.getValue().sellingPriceProperty());

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
                } else if (item.getQuantity().toLowerCase().contains(lowerCaseFilter)) {
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


        add(new Item("R1", "RAM",10, "none",  50, 500));
        add(new Item("C1", "CPU", 5,"none",  25, 2500));
        add(new Item("F1", "Fan", 100,"none",  75, 40));
        add(new Item("S1", "SDD", 10,"none",  12.5, 1500));
        add(new Item("H1", "HDD", 15,"none",  50, 400));
        add(new Item("M1", "Mother braod", 5,"none",  50, 3500.80));
        add(new Item("G1", "Graphic card", 8,"none", 50, 7500));
        add(new Item("C2", "Cades", 100,"meters",  42, 10));
        add(new Item("C3", "Cadles", 4,"none",  60, 5000.54));
    }


    public void setMainController(MainController mainController) {
        this.mainController=mainController;
    }
}
