package sample.controllers;

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
    private TextField TxtInvoiceSearch;

    @FXML
    private Spinner<Double> SprPercent;

    @FXML
    private Button BtnInvoiceRemove;

    @FXML
    private Button BtnInvoiceClear;

    @FXML
    private Button BtnInvoicePrint;

    @FXML
    private TableView<Item> TVInvoiceTable;

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

    @FXML
    private Label LblTotal;

    @FXML
    private Pane ChangesNumberOfItems;

    @FXML
    private Spinner<Double> SprNewNumber;

    @FXML
    private Button ChangesNumberButton;

    private ObservableList<Item> itemData;

    private double netoTotal;
    private double bruttoTotal;
    private double percent=1;
    private double oldNumder=0.0;
    private double newNumber=0.0;

    public void add(Item newItem) {
        itemData.add(newItem);
        if(TxtInvoiceSearch.isDisable()||BtnInvoiceRemove.isDisable()||BtnInvoiceClear.isDisable()||BtnInvoicePrint.isDisable()){
            TxtInvoiceSearch.setDisable(false);
            BtnInvoiceRemove.setDisable(false);
            BtnInvoiceClear.setDisable(false);
            BtnInvoicePrint.setDisable(false);
        }
        updateTotal();
    }

    public void remove(Item selectedItem){
        itemData.remove(selectedItem);
        if(itemData.size()==0){
            TxtInvoiceSearch.setDisable(true);
            BtnInvoiceRemove.setDisable(true);
            BtnInvoiceClear.setDisable(true);
            BtnInvoicePrint.setDisable(true);
        }
        updateTotal();
    }

    public List<Item> getItems(){
        return TVInvoiceTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
    }

    public double getNetoTotal() {
        return netoTotal;
    }

    public double getBruttoTotal() {
        return bruttoTotal;
    }

    public double getPercent() {
        return percent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChangesNumberOfItems.setVisible(false);
        initializeSpinners();
        initializeTalbe();
    }

    private void initializeTalbe(){
        itemData= FXCollections.observableArrayList();

        colStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuantity.setCellValueFactory(cellData->cellData.getValue().sellingQuantityProperty());
        colUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());
        colTotal.setCellValueFactory(cellData->cellData.getValue().totalSellingPriceProperty());

        FilteredList<Item> filteredData = new FilteredList<>(itemData, p -> true);
        TxtInvoiceSearch.textProperty().addListener((observable, oldValue, newValue) -> {
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
                }else if (item.getCostQuantity().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (item.getSellingQuantity().toLowerCase().contains(lowerCaseFilter)) {
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

        sortedData.comparatorProperty().bind(TVInvoiceTable.comparatorProperty());

        TVInvoiceTable.setItems(sortedData);
        TVInvoiceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TVInvoiceTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&TVInvoiceTable.getSelectionModel().getSelectedItem()!=null) {
                ChangesNumberOfItems.setVisible(true);
                ChangesNumberButton.setDisable(true);
                oldNumder=TVInvoiceTable.getSelectionModel().getSelectedItem().getSellingPriceDouble();
                SprNewNumber.getEditor().setText(Double.toString(oldNumder));
            }
        });

        add(new Item("R1","RAM","none",10,3,500,1000));
        add(new Item("C1","CPU","none",5,1,2500,5000));
        add(new Item("F1","Fan","none",100,6,40,50));
        add(new Item("S1","SDD","none",10,1,1500,2000));
        add(new Item("H1","HDD","none",15,2,400,700));
        add(new Item("M1","Mother braod","none",5,1,3500,5499.99));
        add(new Item("G1","Graphic card","none",8,1,7500,10000));
        add(new Item("C2","Cades","meters",100,10.524,10,20));
        add(new Item("C3","Cadles","none",4,1,5000.54,7499.99));
    }

    private void initializeSpinners() {
       SprPercent.valueProperty().addListener((obs, oldValue, newValue) ->{
                percent=1+newValue/100;
                updateTotal();
        }
        );
        SprPercent.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("-?\\d+(\\.\\d)?")){
                double newDoubleValue=Double.parseDouble(newValue);
                if(newDoubleValue>100||newDoubleValue<0) {
                    SprPercent.getValueFactory().setValue(0.0);
                    return;
                }
                percent=1+newDoubleValue/100;
                updateTotal();
            }
        });

        SprNewNumber.valueProperty().addListener((obs, oldValue, newValue) ->{
                    newNumber=newValue;
                    if(newNumber!=oldNumder){
                        ChangesNumberButton.setDisable(false);
                    }else{
                        ChangesNumberButton.setDisable(true);
                    }
                    SprNewNumber.getEditor().setText(Double.toString(newNumber));
                }
        );
        SprNewNumber.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("-?\\d+(\\.\\d)?")){
                newNumber=Double.parseDouble(newValue);

                if(newNumber!=oldNumder&&newNumber>=0){
                    ChangesNumberButton.setDisable(false);
                }else{
                    ChangesNumberButton.setDisable(true);
                }
            }
        });
    }

    private void updateTotal(){
        bruttoTotal=0;
        for(Item item: itemData){
            bruttoTotal=bruttoTotal+ item.getSellingPriceDouble();
        }
        netoTotal = bruttoTotal*percent;
        LblTotal.setText("Totale: R"+String.format("%.2f", netoTotal));
    }

    @FXML
    private void removeItemFromInvoice(ActionEvent event){
        List<Item> items = TVInvoiceTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
        for (Item item: items) {
            remove(item);
        }
    }

    @FXML
    private void clearInvoice(ActionEvent event){
        itemData.clear();
        TxtInvoiceSearch.setDisable(true);
        BtnInvoiceRemove.setDisable(true);
        BtnInvoiceClear.setDisable(true);
        BtnInvoicePrint.setDisable(true);
        updateTotal();
    }

    @FXML
    private void printInvoice(ActionEvent event){

    }

    @FXML
    private void ChangesNumber(ActionEvent event) {
        ChangesNumberOfItems.setVisible(false);
        Item selectedItem = TVInvoiceTable.getSelectionModel().getSelectedItem();
        if(newNumber==0)
            remove(selectedItem);
        else
            selectedItem.setSellingQuantity(newNumber);
        updateTotal();
    }

    @FXML
    private void ClosePopUp(ActionEvent event) {
        ChangesNumberOfItems.setVisible(false);
    }

}
