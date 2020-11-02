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
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2){
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
        Label popUpQuotationLbl = new Label("Number of items to add to the quotation:");
        Spinner<Integer> popUpQuotationSpr = new Spinner<>(1,10000,1,1);
        HBox quotationHBox = new HBox(popUpQuotationLbl, popUpQuotationSpr);
        quotationHBox.setSpacing(10);
        popup = new Alert(Alert.AlertType.NONE,"Item");
        popup.setTitle("Quick Quotes - Add Item");
        try {
            ((Stage)popup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popup.setHeaderText("Add item");
        popup.getDialogPane().setContent(quotationHBox);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = table.getSelectionModel().getSelectedItem();
                if (selectedItem!=null) {
                    categoriesController.moveToQuotation(selectedItem,popUpQuotationSpr.getValue());
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
