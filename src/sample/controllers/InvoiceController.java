package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import sample.data.Item;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InvoiceController implements Initializable {

    @FXML
    private TextField TxtSearch;

    @FXML
    private Label LblGrossTotal;

    @FXML
    private Label LblVAT;

    @FXML
    private Label LblTotal;

    @FXML
    private Button BtnRemove;

    @FXML
    private Button BtnClear;

    @FXML
    private Button BtnPrint;

    @FXML
    private TabPane Tables;

    @FXML
    private QuotationController quotationController;

    @FXML
    private CostingSheetController costingSheetController;

    private ObservableList<Item> itemData;
    private FilteredList<Item> filteredData;

    private MainController mainController;

    private double grossTotal = 0.0;
    private double VAT = 0.0;
    private double total = 0.0;

    public void add(Item newItem, int quantity) {
        newItem.setQuantity((newItem.getQuantityInt() + quantity));
        if (!itemData.contains(newItem)) {
            itemData.add(newItem);
            if (TxtSearch.isDisable() || BtnRemove.isDisable() || BtnClear.isDisable() || BtnPrint.isDisable()) {
                TxtSearch.setDisable(false);
                BtnRemove.setDisable(false);
                BtnClear.setDisable(false);
                BtnPrint.setDisable(false);
            }
        }
        updateTotal();
    }

    public void remove(Item selectedItem) {
        selectedItem.setQuantity(0);
        itemData.remove(selectedItem);
        if (itemData.size() == 0) {
            TxtSearch.setDisable(true);
            BtnRemove.setDisable(true);
            BtnClear.setDisable(true);
            BtnPrint.setDisable(true);
        }
        updateTotal();
    }

    public void clearTables() {
        itemData.clear();
        TxtSearch.setDisable(true);
        BtnRemove.setDisable(true);
        BtnClear.setDisable(true);
        BtnPrint.setDisable(true);
        updateTotal();
    }

    public List<Item> getItems() {
        return itemData.stream().collect(Collectors.toList());
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public double getVAT() {
        return VAT;
    }

    public double getTotal() {
        return total;
    }

    public void initialize(URL location, ResourceBundle resources) {
        initializeDataList();

        quotationController.setInvoiceController(this);
        costingSheetController.setInvoiceController(this);
    }

    public void updateTotal() {
        grossTotal = 0;
        for (Item item : itemData) {
            grossTotal = grossTotal + item.getTotalSellingPriceDouble();
        }
        VAT = grossTotal * 0.15;
        total = grossTotal + VAT;
        LblGrossTotal.setText("Gross Total: R" + String.format("%.2f", grossTotal));
        LblTotal.setText("Total: R" + String.format("%.2f", total));
        LblVAT.setText("VAT: R" + String.format("%.2f", VAT));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void CloseCostingPopUp(ActionEvent event) {

    }

    @FXML
    private void clearItems(ActionEvent event) {
        clearTables();
    }

    @FXML
    private void printTable(ActionEvent event) {

    }

    @FXML
    private void removeItem(ActionEvent event) {
        for (Item item : quotationController.getSelectionModel()) {
            remove(item);
        }
    }

    public FilteredList<Item> getItemData() {
        return filteredData;
    }

    private void initializeDataList() {
        itemData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(itemData, p -> true);
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
                } else if (item.getProfitPercent().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getQuantity().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getTotalSellingPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return item.getSellingPrice().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
}
