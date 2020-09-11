package sample.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import sample.dataReader.Item;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InvoiceController implements Initializable {

    @FXML
    private SplitPane invoice;

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
    private TableColumn<Item, String> ColItemName;

    @FXML
    private TableColumn<Item, String> ColNumberOfItems;

    @FXML
    private TableColumn<Item, String> ColUnitOfItems;

    @FXML
    private TableColumn<Item, String> ColPriceOfItem;

    @FXML
    private Label LblTotal;

    private MainController mainController;

    private ObservableList<Item> itemData;

    private double netoTotal;
    private double bruttoTotal;
    private double percent=1;

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

    private void initializeTalbe(){
        itemData= FXCollections.observableArrayList();

        add(new Item("RAM",3,"none",1000));
        add(new Item("CPU",1,"none",5000));
        add(new Item("Fan",6,"none",50));
        add(new Item("SDD",1,"none",2000));
        add(new Item("HDD",3,"none",700));
        add(new Item("Mother braod",1,"none",5499.99));
        add(new Item("Graphic card",1,"none",10000));
        add(new Item("Cades",1.50,"meters",100));
        add(new Item("Case",1,"none",7499.99));

        ColItemName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ColNumberOfItems.setCellValueFactory(cellData -> cellData.getValue().getNumberOfItemsProperty());
        ColUnitOfItems.setCellValueFactory(cellData -> cellData.getValue().getUnitsProperty());
        ColPriceOfItem.setCellValueFactory(cellData -> cellData.getValue().getTotalePriceProperty());
        FilteredList<Item> filteredData = new FilteredList<>(itemData, p -> true);
        TxtInvoiceSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (item.getUnits().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (item.getTotalePrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Item> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(TVInvoiceTable.comparatorProperty());

        TVInvoiceTable.setItems(sortedData);
        TVInvoiceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void updateTotal(){
        bruttoTotal=0;
        for(Item item: itemData){
            bruttoTotal=bruttoTotal+ item.getTotalePriceDoulbe();
        }
        netoTotal = bruttoTotal*percent;
        LblTotal.setText("Totale: R"+String.format("%.2f", netoTotal));
    }

    private void initializeSpinner() {
       SprPercent.valueProperty().addListener((obs, oldValue, newValue) ->{
                percent=1+newValue/100;
                updateTotal();
        }
        );
        SprPercent.onKeyTypedProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue.toString()));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSpinner();
        initializeTalbe();
    }
}
