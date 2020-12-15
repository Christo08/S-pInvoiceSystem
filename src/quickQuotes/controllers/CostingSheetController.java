package quickQuotes.controllers;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quickQuotes.data.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    }


    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController=invoiceController;
        initializeTable();
    }
}
