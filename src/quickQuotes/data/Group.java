package quickQuotes.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import quickQuotes.controllers.CategoriesController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Group {
    private String nameString;
    private double totalCostDouble;
    private double totalSellingDouble;
    private int numberOfItemsInt;

    private TableView<Item> table;
    private TableColumn<Item, String> colStockCode;
    private TableColumn<Item, String> colDescription;
    private TableColumn<Item, String> colQuantity;
    private TableColumn<Item, String> colCostPrice;
    private TableColumn<Item, String> colSellingPrice;

    private ObservableList<Item> itemList;

    private final StringProperty name;
    private final StringProperty totalCost;
    private final StringProperty totalSelling;
    private final StringProperty numberOfItems;

    private ContextMenu contextMenu;
    private Alert popupWarningRemove;
    private Alert popupChangeItem;

    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;

    public Group(String name) {
        this.nameString = name;
        this.totalCostDouble=0.0;
        this.totalSellingDouble=0.0;
        this.numberOfItemsInt=0;

        this.name = new SimpleStringProperty(name);
        this.totalCost = new SimpleStringProperty(String.format("%.2f", this.totalCostDouble));
        this.totalSelling = new SimpleStringProperty(String.format("%.2f", this.totalSellingDouble));
        this.numberOfItems = new SimpleStringProperty((Integer.toString(this.numberOfItemsInt)));
        initializePopup();
        initializeContextMenu();
        initializeTable();
    }

    private void initializeTable() {
        table = new TableView<>();
        table.setEditable(false);
        colStockCode = new TableColumn<>("Stock Code");
        colDescription = new TableColumn<>("Description");
        colQuantity = new TableColumn<>("Quantity");
        colCostPrice = new TableColumn<>("Cost Price");
        colSellingPrice = new TableColumn<>("Selling Price");

        colStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuantity.setCellValueFactory(cellData->cellData.getValue().groupQuantityProperty());
        colCostPrice.setCellValueFactory(cellData->cellData.getValue().costPriceProperty());
        colSellingPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());

        table.getColumns().addAll(colStockCode, colDescription,colQuantity, colCostPrice, colSellingPrice);

        itemList = FXCollections.observableArrayList();

        table.setItems(itemList);
        table.setOnMousePressed(event -> {
            Item selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                contextMenu.hide();
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(table, event.getScreenX(), event.getScreenY());
                }
            }
        });

        colStockCode.setSortType(TableColumn.SortType.ASCENDING);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initializePopup(){
        popupWarningRemove = new Alert(Alert.AlertType.CONFIRMATION);
        popupWarningRemove.setTitle("Quick Quotes - Remove item");
        try {
            ((Stage) popupWarningRemove.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupWarningRemove.setContentText("Are you sure you want to remove the item for the group?");
        popupWarningRemove.setOnHidden(e -> {
            if (popupWarningRemove.getResult() == ButtonType.OK) {
                List<Item> selectedItem = table.getSelectionModel().getSelectedItems();
                for(Item item: selectedItem){
                    removeItem(item);
                }
            }
        });

        Label popUpChangeItemLbl = new Label("Number of items in group:");
        Spinner<Integer> popUpNumberOfItemSpr = new Spinner<>(1,10000,1,1);
        HBox groupNameHBox = new HBox(popUpChangeItemLbl, popUpNumberOfItemSpr);
        groupNameHBox.setSpacing(10);
        popupChangeItem = new Alert(Alert.AlertType.NONE,"Item");
        popupChangeItem.setTitle("Quick Quotes - Remove form group");
        try {
            ((Stage)popupChangeItem.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupChangeItem.setHeaderText("Remove from group");
        popupChangeItem.getDialogPane().setContent(groupNameHBox);
        popupChangeItem.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupChangeItem.setOnShown(e->{
            Item selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                popUpNumberOfItemSpr.getValueFactory().setValue(selectedItem.getGroupQuantityInt());
            }
        });
        popupChangeItem.setOnHidden(e -> {
            if (popupChangeItem.getResult() == ButtonType.APPLY) {
                Item selectedItem = table.getSelectionModel().getSelectedItem();
                if (selectedItem!=null) {
                    numberOfItemsInt-= selectedItem.getGroupQuantityInt();
                    totalCostDouble-=selectedItem.getCostPriceDouble()*selectedItem.getGroupQuantityInt();
                    totalSellingDouble-=selectedItem.getSellingPriceDouble()*selectedItem.getGroupQuantityInt();
                    selectedItem.setGroupQuantity(popUpNumberOfItemSpr.getValue());
                    numberOfItemsInt+= selectedItem.getGroupQuantityInt();
                    totalCostDouble+=selectedItem.getCostPriceDouble()*selectedItem.getGroupQuantityInt();
                    totalSellingDouble+=selectedItem.getSellingPriceDouble()*selectedItem.getGroupQuantityInt();
                    referenceStringProperty();
                }
            }
        });
    }

    private void initializeContextMenu(){
        contextMenu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Remove Item");
        removeMenuItem.setOnAction(event -> {
            popupWarningRemove.showAndWait();
        });
        MenuItem changesQuantityMenuItem = new MenuItem("Changes Quantity");
        changesQuantityMenuItem.setOnAction(event -> {
            popupChangeItem.showAndWait();
        });
        contextMenu.getItems().add(removeMenuItem);
        contextMenu.getItems().add(changesQuantityMenuItem);
        contextMenu.showingProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().remove(removeMenuItem);
            contextMenu.getItems().remove(changesQuantityMenuItem);
            Item selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                contextMenu.getItems().add(removeMenuItem);
                contextMenu.getItems().add(changesQuantityMenuItem);

            }
        });
    }

    public TableView<Item> getTable() {
        return table;
    }

    public void addItem(Item newItem){
        numberOfItemsInt+= newItem.getGroupQuantityInt();
        totalCostDouble+=newItem.getCostPriceDouble()*newItem.getGroupQuantityInt();
        totalSellingDouble+=newItem.getSellingPriceDouble()*newItem.getGroupQuantityInt();
        boolean fond=false;
        for (Item oldItem:itemList) {
            if(oldItem.equals(newItem)){
                fond=true;
                break;
            }
        }
        if(!fond) {
            itemList.add(newItem);
        }
        referenceStringProperty();
    }

    public void removeItem(Item newItem){
        numberOfItemsInt-=newItem.getGroupQuantityInt();
        totalCostDouble-=newItem.getCostPriceDouble()*newItem.getGroupQuantityInt();
        totalSellingDouble-=newItem.getSellingPriceDouble()*newItem.getGroupQuantityInt();
        itemList.remove(newItem);
        referenceStringProperty();
    }

    public List<Item> getItemList() {
        return itemList.stream().collect(Collectors.toList());
    }

    public String getNumberOfItemsString() {
        return Integer.toString(numberOfItemsInt);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getTotalCost() {
        return totalCost.get();
    }

    public StringProperty totalCostProperty() {
        return totalCost;
    }

    public String getTotalSelling() {
        return totalSelling.get();
    }

    public StringProperty totalSellingProperty() {
        return totalSelling;
    }

    public StringProperty numberOfItemsProperty() {
        return numberOfItems;
    }

    private void referenceStringProperty(){
        this.totalCost.setValue(String.format("%.2f", this.totalCostDouble));
        this.totalSelling.setValue(String.format("%.2f", this.totalSellingDouble));
        this.numberOfItems.setValue((Integer.toString(this.numberOfItemsInt)));
    }

    @Override
    public String toString() {
        List<String> itemCode = new ArrayList<>();
        for(Item item: itemList){
            itemCode.add("("+item.getStockCode()+","+item.getGroupQuantity()+")");
        }
        return nameString + " , " + itemCode ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameString, totalCostDouble, totalSellingDouble, numberOfItemsInt, itemList);
    }
}
