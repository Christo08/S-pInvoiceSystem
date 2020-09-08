package sample.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.dataReader.Item;


public class InvoiceController {
    @FXML
    private TextField TxtInvoiceSearch;

    @FXML
    private Spinner<?> SprPercent;

    @FXML
    private Button BtnInvoiceRemove;

    @FXML
    private Button BtnInvoiceClear;

    @FXML
    private Button BtnInvoicePrint;

    @FXML
    private TableView<Item> TVInvoiceTable;

    @FXML
    private TableColumn<Item, String> ColItemName;

    @FXML
    private TableColumn<Item, Double> ColNumberOfItems;

    @FXML
    private TableColumn<Item, String> ColUnitOfItems;

    @FXML
    private TableColumn<Item, Double> ColPriceOfItem;

    private ObservableList<Item> data = FXCollections.observableArrayList();

    @FXML
    private Label LblTotal;

    private MainController mainController;
    private double grossTotal=0;
    private double nettoTotal=0;
    private double percent=0;

    public InvoiceController() {
        add(new Item("Item1",1,"n.a.",10));
        add(new Item("Item2",5,"n.a.",0.5));
        add(new Item("Item3",100,"meter",10));
        add(new Item("Item4",0.5,"liter",100));
        add(new Item("Item5",1,"n.a.",85.00));
        add(new Item("Item6",8,"n.a.",15.50));
    }

    public void initialize(MainController mainController) {
        this.mainController=mainController;
        initialTable();
    }

    private void initialTable() {
        ColItemName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        // ColNumberOfItems.setCellValueFactory(cellData-> cellData.getValue().getNumderOfItemsProperty());
        ColUnitOfItems.setCellValueFactory(cellData-> cellData.getValue().getUnitsProperty());
        //ColPriceOfItem.setCellValueFactory(cellData-> cellData.getValue().getTotalePriceProperty());
        FilteredList<Item> filteredData = new FilteredList<>(data, p->true);
        TxtInvoiceSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item ->{
                if(newValue == null|| newValue.isEmpty()){
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if(item.getName().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                return false;
            });
        });
        SortedList<Item> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TVInvoiceTable.comparatorProperty());
        TVInvoiceTable.setItems(sortedData);
    }

    public void add(Item newItem) {
        data.add(newItem);
        grossTotal+=newItem.getTotalePrice();
        nettoTotal= grossTotal*(1+percent);
        String totalString = String.format("%.2f", nettoTotal);
        Platform.runLater(()->{
            LblTotal.setText("Total: R"+totalString);
            if(TxtInvoiceSearch.isDisable()){
                TxtInvoiceSearch.setDisable(false);
            }
        });
    }

    public void remove(Item item){
        data.remove(item);
        grossTotal-=item.getTotalePrice();
        nettoTotal= grossTotal*(1+percent);
        String totalString = String.format("%.2f", nettoTotal);
        Platform.runLater(()->{
            LblTotal.setText("Total: R"+totalString);
            if(data.size()==0){
                TxtInvoiceSearch.setDisable(false);
            }
        });

    }
}
