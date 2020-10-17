package sample.data;


import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import sample.controllers.CategoriesController;
import sample.data.Item;

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

    private Alert popup;
    private Spinner<Double> popUpProfitSpr;

    public Category(String name, int id,CategoriesController categoriesController) {
        table = new TableView<>();
        table.setEditable(false);
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

        SortedList<Item> sortedData = new SortedList<>(categoriesController.getItemData(id));

        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.setOnMousePressed(event -> {
            Item selectedItem = table.getSelectionModel().getSelectedItem();
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&selectedItem!=null) {
                popUpProfitSpr.getValueFactory().setValue(selectedItem.getProfitPercentDouble());
                popup.show();
            }
        });


        Label popUpProfitLbl = new Label("Profit Percent(%):");
        popUpProfitSpr = new Spinner<>(15.0,100.0,25.0,1.0);
        HBox profitHBox = new HBox(popUpProfitLbl, popUpProfitSpr);
        profitHBox.setSpacing(10);
        popup = new Alert(Alert.AlertType.NONE,
                "Item");
        popup.setHeaderText("Changes item");
        popup.getDialogPane().setContent(profitHBox);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = table.getSelectionModel().getSelectedItem();
                if(selectedItem!=null){
                    selectedItem.setProfitPercent(popUpProfitSpr.getValue());
                }
            }
        });

        this.id=id;
        this.categoriesController=categoriesController;
        this.setId(name);
        this.setText(name);
        this.setContent(table);
        this.setClosable(false);

    }

    public List<Item> getSelectedItems() {
        return  new ArrayList<>(table.getSelectionModel().getSelectedItems());
    }


}
