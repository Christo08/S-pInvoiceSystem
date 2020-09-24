package sample.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.dataReader.Item;

public class Category extends Tab {
    ScrollPane parentPane;
    TableView<Item> table;
    private TableColumn<Item, String> colStockCode;
    private TableColumn<Item, String> colDescription;
    private TableColumn<Item, String> colUnit;
    private TableColumn<Item, String> colProfit;
    private TableColumn<Item, String> colCostPrice;
    private TableColumn<Item, String> colSellingPrice;
    private final ObservableList<Item> data= FXCollections.observableArrayList(
            new Item("C1", "CPU", "Box", 25,  15000),
            new Item("C2", "Case", "Box", 25,  7500),
            new Item("C3", "CPU Fan", "Box", 10,  250),
            new Item("L1", "LAN Cables", "meters", 25,  100),
            new Item("L2", "Lights", "meters", 60,  500),
            new Item("F1", "Fans", "meters", 25,  50),
            new Item("M1", "Motherboard", "Box", 25,  10000),
            new Item("H1", "HDD", "Box", 50,  1299),
            new Item("S1", "SDD", "Box", 50,  1500),
            new Item("P1", "Power supply", "Box", 12.5,  1000),
            new Item("G1", "Graphics card", "Box", 50,  15000),
            new Item("R1", "RAM", "Box", 25,  500)
    );

    public Category() {
        table = new TableView<>();
        table.setEditable(false);
        table.setFixedCellSize(50);
        table.setPrefWidth(580);
        table.setMaxWidth(580);
        table.setMinWidth(580);
        colStockCode = new TableColumn<>("Stock Code");
        colDescription = new TableColumn<>("Description");
        colUnit = new TableColumn<>("Unit");
        colProfit = new TableColumn<>("Profit (%)");
        colCostPrice = new TableColumn<>("Cost Price");
        colSellingPrice = new TableColumn<>("Selling Price");

        colStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colProfit.setCellValueFactory(cellData->cellData.getValue().profitPercentProperty());
        colCostPrice.setCellValueFactory(cellData->cellData.getValue().costPriceProperty());
        colSellingPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());

        table.getColumns().addAll(colStockCode, colDescription, colUnit,colProfit,colCostPrice,colSellingPrice);

        table.setItems(data);

        this.setText("Test");
        this.setContent(table);
    }

}
