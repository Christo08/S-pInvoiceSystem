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

public class QuotationController implements Initializable {


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

    private InvoiceController invoiceController;

    private int oldNumber=0;
    private int newNumber=0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QuotationPopUp.setVisible(false);
        initializeSpinners();
    }

    @FXML
    private void ChangesQuotation(ActionEvent event) {
        QuotationPopUp.setVisible(false);
        Item selectedItem = TVQuotationTable.getSelectionModel().getSelectedItem();
        if(SprQuotationQuantity.getValue()==0)
            invoiceController.remove(selectedItem);
        selectedItem.setQuantity(SprQuotationQuantity.getValue());
        invoiceController.updateTotal();
    }

    @FXML
    private void CloseQuotationPopUp(ActionEvent event) {
        QuotationPopUp.setVisible(false);
    }

    private void initializeTable(){

        colQuotationStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colQuotationDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuotationQuantity.setCellValueFactory(cellData->cellData.getValue().quantityProperty());
        colQuotationUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colSellingPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());
        colTotalSellingPrice.setCellValueFactory(cellData->cellData.getValue().totalSellingPriceProperty());

        colQuotationStockCode.setSortType(TableColumn.SortType.ASCENDING);

        SortedList<Item> sortedData = new SortedList<>(invoiceController.getItemData());

        sortedData.comparatorProperty().bind(TVQuotationTable.comparatorProperty());

        TVQuotationTable.setItems(sortedData);
        TVQuotationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TVQuotationTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&TVQuotationTable.getSelectionModel().getSelectedItem()!=null) {
                QuotationPopUp.setVisible(true);
                BtnQuotationChanges.setDisable(true);
                Item selectItem = TVQuotationTable.getSelectionModel().getSelectedItem();
                oldNumber=selectItem.getQuantityInt();
                lblQuotationQuantity.setText(lblQuotationQuantity.getText().replace("{{0}}",selectItem.getUnit()));
                SprQuotationQuantity.getValueFactory().setValue(selectItem.getQuantityInt());
            }
        });

    }

    private void initializeSpinners() {
        SprQuotationQuantity.setEditable(true);
        SprQuotationQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10000,1,1));
        SprQuotationQuantity.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,9}?")) {
                SprQuotationQuantity.getEditor().setText(oldValue);
            }else{
                int newQuantity=0;
                if(!newValue.isEmpty()){
                    newQuantity = Integer.parseInt(newValue);
                }
                SprQuotationQuantity.getEditor().setText(newValue);
                SprQuotationQuantity.getValueFactory().setValue(newQuantity);
            }
        });
        SprQuotationQuantity.valueProperty().addListener((obs,oldValue,newValue)->{
            if(oldNumber==newValue){
                BtnQuotationChanges.setDisable(true);
            }else{
                BtnQuotationChanges.setDisable(false);
            }
        });
    }

    public List<Item> getSelectionModel() {
        return TVQuotationTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
    }

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController=invoiceController;
        initializeTable();
    }

}
