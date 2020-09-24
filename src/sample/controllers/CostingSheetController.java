package sample.controllers;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import sample.dataReader.Item;

import javax.sound.midi.Soundbank;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CostingSheetController implements Initializable {
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
    private Button BtnCostingChanges;

    @FXML
    private Spinner<Integer> SprCostingQuantity;

    @FXML
    private Spinner<Double> SprProfitPercent;

    private InvoiceController invoiceController;
    private int oldQuantity;
    private double oldProfit;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CostingPopUp.setVisible(false);
        initializeSpinners();
    }

    @FXML
    private void changesCosting(ActionEvent event) {
        CostingPopUp.setVisible(false);
        Item selectedItem = TVCostTable.getSelectionModel().getSelectedItem();
        if(SprCostingQuantity.getValue() ==0)
            invoiceController.remove(selectedItem);
        selectedItem.setQuantity(SprCostingQuantity.getValue());
        selectedItem.setProfitPercent(SprProfitPercent.getValue());
        invoiceController.updateTotal();
    }

    @FXML
    private void CloseCostingPopUp(ActionEvent event) {
        CostingPopUp.setVisible(false);
    }

    private void initializeTable(){

        colCostStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colCostDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colCostQuantity.setCellValueFactory(cellData->cellData.getValue().quantityProperty());
        colCostUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colCostProfit.setCellValueFactory(cellData->cellData.getValue().profitPercentProperty());
        colCostPrice.setCellValueFactory(cellData->cellData.getValue().costPriceProperty());
        colCostTotalPrice.setCellValueFactory(cellData->cellData.getValue().totalCostPriceProperty());

        colCostStockCode.setSortType(TableColumn.SortType.ASCENDING);


        SortedList<Item> sortedData = new SortedList<>(invoiceController.getItemData());

        sortedData.comparatorProperty().bind(TVCostTable.comparatorProperty());

        TVCostTable.setItems(sortedData);
        TVCostTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TVCostTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&TVCostTable.getSelectionModel().getSelectedItem()!=null) {
                CostingPopUp.setVisible(true);
                BtnCostingChanges.setDisable(true);
                Item selectItem = TVCostTable.getSelectionModel().getSelectedItem();
                oldQuantity =selectItem.getQuantityInt();
                oldProfit =selectItem.getProfitPercentDouble();
                lblCostingQuantity.setText(lblCostingQuantity.getText().replace("{{1}}",selectItem.getUnit()));
                SprCostingQuantity.getValueFactory().setValue(oldQuantity);
                SprProfitPercent.getValueFactory().setValue(oldProfit);
            }
        });

    }

    private void initializeSpinners() {
        SprCostingQuantity.setEditable(true);
        SprCostingQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10000,1,1));
        SprCostingQuantity.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,9}?")) {
                SprCostingQuantity.getEditor().setText(oldValue);
            }else{
                int newQuantity=0;
                if(!newValue.isEmpty()){
                    newQuantity = Integer.parseInt(newValue);
                }
                SprCostingQuantity.getEditor().setText(newValue);
                SprCostingQuantity.getValueFactory().setValue(newQuantity);
            }
        });
        SprCostingQuantity.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(oldQuantity==newValue && oldProfit==SprProfitPercent.getValue()){
                    BtnCostingChanges.setDisable(true);
                }else{
                    BtnCostingChanges.setDisable(false);
                }
        });

        SprProfitPercent.setEditable(true);
        SprProfitPercent.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0,100,1,0.1));
        SprProfitPercent.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                SprProfitPercent.getEditor().setText(oldValue);
            }else{
                double newProfit=1;
                if(!newValue.isEmpty()){
                    newProfit = Double.parseDouble(newValue);
                    if(newProfit<=0){
                        newProfit=1;
                    }
                }
                SprProfitPercent.getEditor().setText(newValue);
                SprProfitPercent.getValueFactory().setValue(newProfit);
            }
        });
        SprProfitPercent.valueProperty().addListener((obs,oldValue,newValue)->{
            if(oldQuantity==SprCostingQuantity.getValue() && oldProfit==newValue){
                BtnCostingChanges.setDisable(true);
            }else{
                BtnCostingChanges.setDisable(false);
            }
        });

    }

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController=invoiceController;
        initializeTable();
    }
}
