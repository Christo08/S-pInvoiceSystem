package sample.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import sample.dataReader.Item;

import java.util.ArrayList;
import java.util.List;

public class Category extends Tab {
    TableView<Item> table;
    private TableColumn<Item, String> colStockCode;
    private TableColumn<Item, String> colDescription;
    private TableColumn<Item, String> colUnit;
    private TableColumn<Item, String> colProfit;
    private TableColumn<Item, String> colCostPrice;
    private TableColumn<Item, String> colSellingPrice;
    private CategoriesController categoriesController;
    private ObservableList<Item> data;
    private int id;

    public Category(String name, int id,CategoriesController categoriesController) {
        this.id=id;
        this.categoriesController=categoriesController;
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

        colStockCode.setSortType(TableColumn.SortType.ASCENDING);

        SortedList<Item> sortedData = new SortedList<>(this.categoriesController.getItemData(id));

        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.setId(name);
        this.setText(name);
        this.setContent(table);
    }

    public List<Item> getSelectedItems() {
        return  new ArrayList<>(table.getSelectionModel().getSelectedItems());
    }


}
