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
import javafx.scene.layout.Pane;
import sample.dataReader.Item;

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
    private TableView<Item> TVQuotationTable;

    @FXML
    private TableColumn<Item, String> colQuotationStockCode;

    @FXML
    private TableColumn<Item, String> colQuotationDescription;

    @FXML
    private TableColumn<Item, String> colQuotationQuantity;

    @FXML
    private TableColumn<Item, String> colQuotationUnit;

    @FXML
    private TableColumn<Item, String> colSellingPrice;

    @FXML
    private TableColumn<Item, String> colTotalSellingPrice;

    @FXML
    private Pane QuotationPopUp;

    @FXML
    private Label lblQuotationQuantity;

    @FXML
    private Spinner<Integer> SprQuotationQuantity;

    @FXML
    private Button BtnQuotationChanges;

    @FXML
    private TableView<Item> TVCostTable;

    @FXML
    private TableColumn<Item, String> colCostStockCode;

    @FXML
    private TableColumn<Item, String> colCostDescription;

    @FXML
    private TableColumn<Item, String> colCostQuantity;

    @FXML
    private TableColumn<Item, String> colCostUnit;

    @FXML
    private TableColumn<Item, String> colCostProfit;

    @FXML
    private TableColumn<Item, String> colCostPrice;

    @FXML
    private TableColumn<Item, String> colCostTotalPrice;

    @FXML
    private Pane CostingPopUp;

    @FXML
    private Label lblCostingQuantity;

    @FXML
    private Spinner<Integer> SprCostingQuantity;

    @FXML
    private Spinner<Double> SprProfitPercent;

    @FXML
    private Button BtnCostingChanges;

    @FXML
    private Button BtnCostingCancel;

    private ObservableList<Item> itemData;

    private MainController mainController;

    private double grossTotal=0.0;
    private double VAT=0.0;
    private double total=0.0;
    private int oldNumber=0;
    private int newNumber=0;

    public void add(Item newItem, int quantity) {
        newItem.setQuantity(quantity);
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

    public void remove(Item selectedItem){
        selectedItem.setQuantity(0);
        itemData.remove(selectedItem);
        if(itemData.size()==0){
            TxtSearch.setDisable(true);
            BtnRemove.setDisable(true);
            BtnClear.setDisable(true);
            BtnPrint.setDisable(true);
        }
        mainController.addToTab(selectedItem);
        updateTotal();
    }

    public void clearTables(){
        for (Item item:itemData) {
            item.setQuantity(0);
            mainController.addToTab(item);
        }
        itemData.clear();
        TxtSearch.setDisable(true);
        BtnRemove.setDisable(true);
        BtnClear.setDisable(true);
        BtnPrint.setDisable(true);
        updateTotal();
    }

    public List<Item> getItems(){
        return TVQuotationTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
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
        QuotationPopUp.setVisible(false);
        initializeSpinners();
        initializeTalbe();
    }

    private void initializeTalbe(){
        itemData= FXCollections.observableArrayList();

        colQuotationStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colQuotationDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuotationQuantity.setCellValueFactory(cellData->cellData.getValue().quantityProperty());
        colQuotationUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colSellingPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());
        colTotalSellingPrice.setCellValueFactory(cellData->cellData.getValue().totalSellingPriceProperty());

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
                }else if (item.getUnit().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (item.getQuantity().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (item.getTotalSellingPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (item.getSellingPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Item> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(TVQuotationTable.comparatorProperty());

        TVQuotationTable.setItems(sortedData);
        TVQuotationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TVQuotationTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&TVQuotationTable.getSelectionModel().getSelectedItem()!=null) {
                QuotationPopUp.setVisible(true);
                BtnQuotationChanges.setDisable(true);
                Item selectItem = TVQuotationTable.getSelectionModel().getSelectedItem();
                oldNumber=selectItem.getQuantityInt();
                SprQuotationQuantity.getEditor().setText(Double.toString(oldNumber));
                lblQuotationQuantity.setText(lblQuotationQuantity.getText().replace("{{0}}",selectItem.getUnit()));
            }
        });

    }

    private void initializeSpinners() {
        SprQuotationQuantity.valueProperty().addListener((obs, oldValue, newValue) ->{
                    newNumber=newValue;
                    if(newNumber!=oldNumber){
                        BtnQuotationChanges.setDisable(false);
                    }else{
                        BtnQuotationChanges.setDisable(true);
                    }
                    SprQuotationQuantity.getEditor().setText(Double.toString(newNumber));
                }
        );
        SprQuotationQuantity.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null) {
                    return ;
                }
                try {
                    newNumber = Integer.parseInt(newValue);
                    if(newNumber!=oldNumber&&newNumber>=0){
                        BtnQuotationChanges.setDisable(false);
                    }else{
                        BtnQuotationChanges.setDisable(true);
                    }
                } catch (NumberFormatException nfe) {
                    return ;
                }
            }
        });
    }

    private void updateTotal(){
        grossTotal=0;
        for(Item item: itemData){
            grossTotal=grossTotal+ item.getTotalSellingPriceDouble();
        }
        VAT=grossTotal*0.15;
        total=grossTotal+VAT;
        LblGrossTotal.setText("Gross Total: R"+String.format("%.2f", grossTotal));
        LblTotal.setText("Total: R"+String.format("%.2f", total));
        LblVAT.setText("VAT: R"+String.format("%.2f", VAT));
    }

    public void setMainController(MainController mainController) {
        this.mainController=mainController;
    }

    @FXML
    private void ChangesQuotation(ActionEvent event) {
        QuotationPopUp.setVisible(false);
        Item selectedItem = TVQuotationTable.getSelectionModel().getSelectedItem();
        if(newNumber==0)
            remove(selectedItem);
        selectedItem.setQuantity(newNumber);
        updateTotal();
    }

    @FXML
    private void changesCosting(ActionEvent event){

    }

    @FXML
    private void CloseCostingPopUp(ActionEvent event) {

    }

    @FXML
    private void CloseQuotationPopUp(ActionEvent event) {
        QuotationPopUp.setVisible(false);
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
        List<Item> items = TVQuotationTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
        for (Item item: items) {
            remove(item);
        }
    }


}
