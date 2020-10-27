package quickQuotes.data;


import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import quickQuotes.controllers.CategoriesController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private int index;

    private Alert popup;
    private Spinner<Double> popUpProfitSpr;

    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;

    public Category(String name, int index,CategoriesController categoriesController) {
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

        SortedList<Item> sortedData = new SortedList<>(categoriesController.getItemData(name));

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
        initializePopup();

        this.index=index;
        this.categoriesController=categoriesController;
        this.setId(name);
        this.setText(name);
        this.setContent(table);
        this.setClosable(false);
    }

    private void initializePopup(){
        Label popUpProfitLbl = new Label("Profit Percent(%):");
        popUpProfitSpr = new Spinner<>(15.0,100.0,25.0,1.0);
        HBox profitHBox = new HBox(popUpProfitLbl, popUpProfitSpr);
        profitHBox.setSpacing(10);
        popup = new Alert(Alert.AlertType.NONE,"Item");
        popup.setTitle("Quick Quotes - Changes Profit Percent");
        try {
            ((Stage)popup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popup.setHeaderText("Changes item");
        popup.getDialogPane().setContent(profitHBox);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = table.getSelectionModel().getSelectedItem();
                if(selectedItem!=null){
                    selectedItem.setProfitPercent(popUpProfitSpr.getValue());
                    categoriesController.refresh(selectedItem);
                }
            }
        });
    }

    public List<Item> getSelectedItems() {
        return  new ArrayList<>(table.getSelectionModel().getSelectedItems());
    }


    public int getIndex() {
        return index;
    }
}
