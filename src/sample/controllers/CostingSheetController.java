package sample.controllers;

import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.data.Item;

import java.net.URL;
import java.util.ResourceBundle;

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

    private Alert popup;
    private GridPane popUpPane;
    private HBox quantityHBox;
    private String popUpQuantityLblString;
    private Label popUpQuantityLbl;
    private Spinner<Integer> popUpQuantitySpr;
    private HBox profitHBox;
    private Label popUpProfitLbl;
    private Spinner<Double> popUpProfitSpr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePopUp();
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
          Item selectedItem = TVCostTable.getSelectionModel().getSelectedItem();
          if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&selectedItem!=null) {
              popUpQuantityLbl.setText(popUpQuantityLblString.replace("{{0}}",selectedItem.getUnit()));
              popUpProfitSpr.getValueFactory().setValue(selectedItem.getProfitPercentDouble());
              popUpQuantitySpr.getValueFactory().setValue(selectedItem.getQuantityInt());
              popUpQuantitySpr.setStyle("");
              popup.show();
          }
        });

    }

    private void initializePopUp() {
        popUpProfitLbl = new Label("Profit Percent(%):");
        popUpProfitSpr = new Spinner<>(15.0,100.0,25.0,1.0);
        popUpProfitSpr.setEditable(true);
        popUpProfitSpr.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;
            String newText =popUpProfitSpr.getEditor().getText();
            try
            {
                // checking valid integer using parseInt() method
                double newNumber = Double.parseDouble(newText);
                if(newNumber>=15&&newNumber<=100){
                    popUpProfitSpr.getValueFactory().setValue(newNumber);
                }else{
                    popUpProfitSpr.setStyle("-fx-border-color: red");
                }
            }
            catch (NumberFormatException e)
            {
                popUpProfitSpr.setStyle("-fx-border-color: red");
            }
        });

        popUpQuantityLblString="Quantity ({{0}}):";
        popUpQuantityLbl = new Label(popUpQuantityLblString);
        popUpQuantitySpr = new Spinner<>(1,9999999,1,1);
        popUpQuantitySpr.setEditable(true);
        popUpQuantitySpr.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;
            String newText =popUpQuantitySpr.getEditor().getText();
            try
            {
                // checking valid integer using parseInt() method
                int newNumber = Integer.parseInt(newText);
                if(newNumber>=1&&newNumber<=9999999){
                    popUpQuantitySpr.getValueFactory().setValue(newNumber);
                }else{
                    popUpQuantitySpr.setStyle("-fx-border-color: red");
                }
            }
            catch (NumberFormatException e)
            {
                popUpQuantitySpr.setStyle("-fx-border-color: red");
            }
        });

        popUpPane = new GridPane();
        popUpPane.setAlignment(Pos.CENTER);
        popUpPane.setVgap(10);
        popUpPane.setHgap(10);
        popUpPane.add(popUpProfitLbl,0,0);
        popUpPane.add(popUpProfitSpr,1,0);
        popUpPane.add(popUpQuantityLbl,0,1);
        popUpPane.add(popUpQuantitySpr,1,1);
        popup = new Alert(Alert.AlertType.NONE,
                "Item");
        popup.setHeaderText("Changes item");
        popup.getDialogPane().setContent(popUpPane);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = TVCostTable.getSelectionModel().getSelectedItem();
                if(selectedItem!=null){
                    selectedItem.setProfitPercent(popUpProfitSpr.getValue());
                    selectedItem.setQuantity(popUpQuantitySpr.getValue());
                    invoiceController.updateTotal();
                }
            }
        });
    }

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController=invoiceController;
        initializeTable();
    }
}
