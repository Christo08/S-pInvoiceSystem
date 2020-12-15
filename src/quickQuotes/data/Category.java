package quickQuotes.data;


import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quickQuotes.controllers.CategoriesController;
import quickQuotes.controllers.SettingsFileController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Category extends Tab {
    private TableView<Item> table;
    private TableColumn<Item, String> colStockCode;
    private TableColumn<Item, String> colDescription;
    private TableColumn<Item, String> colUnit;
    private TableColumn<Item, String> colProfit;
    private TableColumn<Item, String> colCostPrice;
    private TableColumn<Item, String> colSellingPrice;
    private CategoriesController categoriesController;
    private ObservableList<Item> data;
    private int index;

    private ComboBox<String> popUpGroupNameCmb;
    private ContextMenu contextMenu;
    private Alert popupQuotation;
    private Alert popupGroup;

    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();

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

        SortedList<Item> sortedData = new SortedList<>(categoriesController.getItemData(name));

        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);

        colStockCode.setSortType(TableColumn.SortType.ASCENDING);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.setOnMousePressed(event -> {
            Item selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                contextMenu.hide();
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    popupQuotation.show();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    popUpGroupNameCmb.getItems().clear();
                    popUpGroupNameCmb.getItems().addAll(categoriesController.getGroupsName());
                    contextMenu.show(table, event.getScreenX(), event.getScreenY());
                }
            }
        });

        initializePopup();
        initializeContextMenu();

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
        popupQuotation = new Alert(Alert.AlertType.NONE,"Item");
        popupQuotation.setTitle("Quick Quotes - Add Item");
        try {
            ((Stage)popupQuotation.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupQuotation.setHeaderText("Add item");
        popupQuotation.getDialogPane().setContent(quotationHBox);
        popupQuotation.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupQuotation.setOnHidden(e -> {
            if (popupQuotation.getResult() == ButtonType.APPLY) {
                List<Item> selectedItems = table.getSelectionModel().getSelectedItems();
                if (selectedItems!=null) {
                    for (Item item:selectedItems) {
                        categoriesController.moveToQuotation(item,popUpQuotationSpr.getValue());
                    }
                }
            }
        });

        Label popUpGroupLbl = new Label("Name of group:");
        popUpGroupNameCmb = new ComboBox<>();
        HBox groupNameHBox = new HBox(popUpGroupLbl, popUpGroupNameCmb);
        groupNameHBox.setSpacing(10);
        Label popUpNumberOfItemsLbl = new Label("Number of Items:");
        Spinner<Integer> popUppNumberOfItemsSpr = new Spinner<>(1,10000,1,1);
        HBox groupNumberOfItemsHBox = new HBox(popUpNumberOfItemsLbl, popUppNumberOfItemsSpr);
        groupNumberOfItemsHBox.setSpacing(10);
        VBox groupVBox = new VBox(groupNameHBox,groupNumberOfItemsHBox);
        groupVBox.setSpacing(10);
        popupGroup = new Alert(Alert.AlertType.NONE,"Item");
        popupGroup.setTitle("Quick Quotes - Add to group");
        try {
            ((Stage)popupGroup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupGroup.setHeaderText("Add to group");
        popupGroup.getDialogPane().setContent(groupVBox);
        popupGroup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupGroup.setOnHidden(e -> {
            if (popupGroup.getResult() == ButtonType.APPLY) {
                ObservableList<Item> selectedItems = table.getSelectionModel().getSelectedItems();
                for (Item selectedItem:selectedItems) {
                    Item newItem = new Item(selectedItem);
                    newItem.setGroupQuantity(popUppNumberOfItemsSpr.getValue());
                    categoriesController.addToGroup(newItem, popUpGroupNameCmb.getValue());
                }
            }
        });
    }

    private void initializeContextMenu(){
        contextMenu = new ContextMenu();
        MenuItem addToQuotationMenuItem = new MenuItem("Add to quotation");
        addToQuotationMenuItem.setOnAction(event -> {
            popupQuotation.showAndWait();
        });
        MenuItem addToGroupMenuItem = new MenuItem("Add to group");
        addToGroupMenuItem.setOnAction(event -> {
            popupGroup.showAndWait();
        });
        contextMenu.getItems().add(addToQuotationMenuItem);
        contextMenu.getItems().add(addToGroupMenuItem);
        contextMenu.showingProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().remove(addToGroupMenuItem);
            if(categoriesController.hasGroups()){
                contextMenu.getItems().add(addToGroupMenuItem);
            }
        });
    }

    public List<Item> getSelectedItems() {
        return  new ArrayList<>(table.getSelectionModel().getSelectedItems());
    }

}
