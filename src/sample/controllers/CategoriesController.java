package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import sample.data.Item;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriesController implements Initializable {

    @FXML
    private TextField TxtSearch;

    @FXML
    private Button BtnAddToInvoice;

    @FXML
    private Button BtnResetItems;

    @FXML
    private TabPane Tables;

    private MainController mainController;
    private List<Category> categories;
    private List<ObservableList<Item> > items;
    private List<FilteredList<Item>> filteredItems;

    private int numberOfCategories =0;

    public CategoriesController() {
        categories = new ArrayList<>();
        items= new ArrayList<>();
        filteredItems = new ArrayList<>();
    }

    public void setMainController(MainController mainController) {
        this.mainController=mainController;
    }

    @FXML
    private void moveItemToInvoice(ActionEvent event) {
        int index =Tables.getSelectionModel().getSelectedIndex();
        List<Item>items =categories.get(index).getSelectedItems();
        List<Integer>quantities =new ArrayList<>();
        items.forEach(x->quantities.add(1));
        mainController.addToInvoice(items,quantities);
    }

    @FXML
    private void resetItems(ActionEvent event) {
        Tables.getTabs().removeAll(categories);
        categories = new ArrayList<>();
        BtnAddToInvoice.setDisable(true);
        BtnResetItems.setDisable(true);
        TxtSearch.setDisable(true);
    }

    public void addTab(String catagory, List<Item> items){
        numberOfCategories++;
        ObservableList<Item> newItems = FXCollections.observableArrayList(items);
        this.items.add(newItems);
        FilteredList<Item> newFilteredData = new FilteredList<>(newItems, p -> true);
        filteredItems.add(newFilteredData);
        Category newCategory=new Category(catagory,numberOfCategories,this);
        BtnAddToInvoice.setDisable(false);
        BtnResetItems.setDisable(false);
        TxtSearch.setDisable(false);
        categories.add(newCategory);
        Tables.getTabs().add(newCategory);
    }
    public FilteredList<Item> getItemData( int index) {
        return filteredItems.get(index-1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDataList();
    }

    private void initializeDataList() {
        TxtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.get(Tables.getSelectionModel().getSelectedIndex()).setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getStockCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getUnit().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getProfitPercent().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getCostPrice().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return item.getSellingPrice().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
}
