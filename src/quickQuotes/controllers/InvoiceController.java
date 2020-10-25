package quickQuotes.controllers;

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
import quickQuotes.data.CostingSheet;
import quickQuotes.data.Item;
import quickQuotes.data.Quotation;

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
    private TabPane Tables;

    private ObservableList<Item> itemData;
    private FilteredList<Item> filteredData;

    private MainController mainController;
    private Quotation quotationTab;
    private CostingSheet costingSheetTab;

    private double grossTotal = 0.0;
    private double VAT = 0.0;
    private double total = 0.0;

    public void add(Item newItem, int quantity) {
        newItem.setQuantity((newItem.getQuantityInt() + quantity));
        if(itemData.size()==0){
            itemData.add(newItem);
            TxtSearch.setDisable(false);
            BtnRemove.setDisable(false);
            BtnClear.setDisable(false);
            quotationTab = new Quotation(this);
            costingSheetTab = new CostingSheet(this);
            Tables.getTabs().add(quotationTab);
            Tables.getTabs().add(costingSheetTab);
        }else{
            if(!itemData.contains(newItem)){
                itemData.add(newItem);
            }
        }
        updateTotal();
    }

    public void add(Item newItem) {
        if(itemData.size()==0){
            itemData.add(newItem);
            TxtSearch.setDisable(false);
            BtnRemove.setDisable(false);
            BtnClear.setDisable(false);
            quotationTab = new Quotation(this);
            costingSheetTab = new CostingSheet(this);
            Tables.getTabs().add(quotationTab);
            Tables.getTabs().add(costingSheetTab);
        }else{
            if(itemData.contains(newItem)){
                itemData.remove(newItem);
            }else{
                itemData.add(newItem);
            }
        }
        updateTotal();
    }

    public void remove(Item selectedItem) {
        selectedItem.setQuantity(0);
        itemData.remove(selectedItem);
        if (itemData.size() == 0) {
            Tables.getTabs().remove(quotationTab);
            Tables.getTabs().remove(costingSheetTab);
            TxtSearch.setDisable(true);
            BtnRemove.setDisable(true);
            BtnClear.setDisable(true);
        }
        updateTotal();
    }

    public void clearTables() {

        List<Item> items = itemData.stream().collect(Collectors.toList());
        for (Item item : items) {
            remove(item);
        }
        Tables.getTabs().remove(quotationTab);
        Tables.getTabs().remove(costingSheetTab);
        TxtSearch.setDisable(true);
        BtnRemove.setDisable(true);
        BtnClear.setDisable(true);
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
    private void clearItems(ActionEvent event) {
        clearTables();
    }

    @FXML
    private void removeItem(ActionEvent event) {
        for (Item item : quotationTab.getSelectionModel()) {
            remove(item);
        }
        for (Item item : costingSheetTab.getSelectionModel()) {
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

    public void refresh(Item selectedItem) {
        if(itemData.contains(selectedItem)){
            itemData.remove(selectedItem);
            itemData.add(selectedItem);
            updateTotal();
        }
    }
}
