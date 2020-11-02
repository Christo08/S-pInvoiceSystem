package quickQuotes.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import quickQuotes.data.Category;
import quickQuotes.data.Item;
import quickQuotes.data.MainCategory;

import java.net.URL;
import java.util.*;

public class CategoriesController implements Initializable {

    @FXML
    private TextField TxtSearch;

    @FXML
    private Button BtnAddToQuotation;

    @FXML
    private Button BtnResetItems;

    @FXML
    private TabPane Tables;

    private MainController mainController;
    private List<MainCategory> mainCategories;
    private Map<String,ObservableList<Item> > items;
    private Map<String,FilteredList<Item>> filteredItems;

    private int numberOfCategories =0;

    public CategoriesController() {
        items= new HashMap<>();
        filteredItems = new HashMap<>();
        mainCategories = new ArrayList<>();
    }

    public void setMainController(MainController mainController) {
        this.mainController=mainController;
    }

    @FXML
    private void moveItemToQuotation(ActionEvent event) {
        int index =Tables.getSelectionModel().getSelectedIndex();
        List<Item>items =mainCategories.get(index).getSelectedItems();
        List<Integer>quantities =new ArrayList<>();
        items.forEach(x->quantities.add(1));
        mainController.addToInvoice(items,quantities);
    }

    @FXML
    private void resetItems(ActionEvent event) {
        TxtSearch.clear();
        for (MainCategory mainCategory:mainCategories){
            mainCategory.removeTabs();
            Tables.getTabs().remove(mainCategory);
        }
        items= new HashMap<>();
        filteredItems = new HashMap<>();
        mainCategories = new ArrayList<>();
        numberOfCategories =0;
        BtnAddToQuotation.setDisable(true);
        BtnResetItems.setDisable(true);
        TxtSearch.setDisable(true);
        mainController.reset();
    }

    public void addTab(String categoryName, List<Item> items){
        if(categoryName.contains("Solar Pane")) {
            createAddMainCategory("Solar Pane", categoryName, items);
        }else if(categoryName.contains("Inverter")){
            createAddMainCategory("Inverter", categoryName, items);
        }else if(categoryName.contains("Solar Batter")){
            createAddMainCategory("Solar Batter", categoryName, items);
        }else if(categoryName.contains("Components")){
            createAddMainCategory("Components", categoryName, items);
        }else{
            createAddMainCategory("Other", categoryName, items);
        }
        if (mainCategories.size()!=0){
            BtnAddToQuotation.setDisable(false);
            BtnResetItems.setDisable(false);
            TxtSearch.setDisable(false);
        }
    }

    private void createAddMainCategory(String mainCategoryName, String categoryName, List<Item> items){
        boolean findMainCategory = mainCategories.parallelStream().anyMatch(mainCategory -> mainCategory.getText().contains(mainCategoryName));
        MainCategory mainCategory;
        if(findMainCategory){
            mainCategory=mainCategories.stream().filter(inMainCategory -> inMainCategory.getText().contains(mainCategoryName)).findFirst().orElse(null);
        }else{
            mainCategory = new MainCategory(mainCategoryName);
            mainCategories.add(mainCategory);
        }
        numberOfCategories++;
        ObservableList<Item> newItems = FXCollections.observableArrayList(items);
        this.items.put(categoryName,newItems);
        FilteredList<Item> newFilteredData = new FilteredList<>(newItems, p -> true);
        filteredItems.put(categoryName,newFilteredData);
        Category newCategory=new Category(categoryName,numberOfCategories,this);
        mainCategory.addSubCategory(newCategory);
        sortTabs();
    }

    private void sortTabs() {
        mainCategories.sort(Comparator.comparing(Tab::getText));
        Tables.getTabs().clear();
        Tables.getTabs().setAll(mainCategories);
    }

    public FilteredList<Item> getItemData( String id) {
        return filteredItems.get(id);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDataList();
    }

    private void initializeDataList() {
        TxtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            for ( FilteredList<Item> items:filteredItems.values()) {
                items.setPredicate(item -> {
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
            }
        });
    }

    public void refresh(Item selectedItem) {
        mainController.refresh(selectedItem);
    }

    public void moveToQuotation(Item newItem, int quantity) {
        mainController.addToInvoice(newItem,quantity);
    }
}
