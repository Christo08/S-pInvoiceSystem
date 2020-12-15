package quickQuotes.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import quickQuotes.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CategoriesController implements Initializable {

    @FXML
    private TextField TxtSearch;

    @FXML
    private Button BtnAddToQuotation;

    @FXML
    private Button BtnCreateGroup;

    @FXML
    private Button BtnResetItems;

    @FXML
    private TabPane Tables;

    private MainController mainController;
    private GroupsTab groupsTab=null;
    private List<Tab> mainCategories;
    private Map<String,ObservableList<Item>> items;
    private Map<String,FilteredList<Item>> filteredItems;
    private ObservableList<Group> groups;
    private FilteredList<Group> filteredGroups;

    private Alert popupCreateGroup;

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
    void createGroup(ActionEvent event) {
        popupCreateGroup.showAndWait();
    }

    @FXML
    private void moveItemToQuotation(ActionEvent event) {
        int index =Tables.getSelectionModel().getSelectedIndex();
        if(mainCategories.get(index) instanceof MainCategory){
            List<Item> items = ((MainCategory)mainCategories.get(index)).getSelectedItems();
            List<Integer> quantities = new ArrayList<>();
            items.forEach(x->quantities.add(1));
            mainController.addToInvoice(items,quantities);
        }else if(mainCategories.get(index) instanceof GroupsTab){
            List<Item> items =((GroupsTab)mainCategories.get(index)).getSelectedItems();
            List<Integer> quantities =new ArrayList<>();
            items.forEach(x->quantities.add(1));
            mainController.addToInvoice(items,quantities);
        }
    }

    @FXML
    private void resetItems(ActionEvent event) {
        TxtSearch.clear();
        for (Tab tab:mainCategories){
            if(tab instanceof MainCategory)
                ((MainCategory)tab).removeTabs();
            Tables.getTabs().remove(tab);
        }
        items= new HashMap<>();
        filteredItems = new HashMap<>();
        mainCategories = new ArrayList<>();
        numberOfCategories =0;
        BtnAddToQuotation.setDisable(true);
        BtnResetItems.setDisable(true);
        BtnCreateGroup.setDisable(true);
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
            BtnCreateGroup.setDisable(false);
            TxtSearch.setDisable(false);
        }
    }

    private void initializePopup(){
        Label popUpGroupNameLbl = new Label("Name of group:");
        TextField popUpGroupNameTxt = new TextField();
        HBox groupNameHBox = new HBox(popUpGroupNameLbl, popUpGroupNameTxt);
        groupNameHBox.setSpacing(10);
        popupCreateGroup = new Alert(Alert.AlertType.NONE,"Create Group");
        popupCreateGroup.setTitle("Quick Quotes - Create Group");
        try {
            ((Stage) popupCreateGroup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupCreateGroup.setHeaderText("Create Group");
        popupCreateGroup.getDialogPane().setContent(groupNameHBox);
        popupCreateGroup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupCreateGroup.setOnShown(e ->{
            popUpGroupNameTxt.clear();
        });
        popupCreateGroup.setOnHidden(e -> {
            if (popupCreateGroup.getResult() == ButtonType.APPLY) {
                boolean nameCheck = groups.stream().anyMatch(group -> group.getName().equals(popUpGroupNameTxt.getText()));
                if (!popUpGroupNameTxt.getText().isEmpty() &&!nameCheck) {

                    Group newGroup =new Group(popUpGroupNameTxt.getText());
                    addNewGroup(newGroup);
                    saveGroups();
                }else if (popUpGroupNameTxt.getText().isEmpty()) {
                    Alert popupError = new Alert(Alert.AlertType.WARNING);
                    popupError.setTitle("Quick Quotes - Group error");
                    try {
                        ((Stage) popupError.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    popupError.setContentText("Please enter a name.");
                    popupError.showAndWait();
                }else if(nameCheck){
                    Alert popupError = new Alert(Alert.AlertType.WARNING);
                    popupError.setTitle("Quick Quotes - Group error");
                    try {
                        ((Stage) popupError.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    popupError.setContentText("Please enter a valid name.");
                    popupError.showAndWait();
                }
            }
        });
    }

    public void addNewGroup(Group newGroup) {
        if(groupsTab==null){
            filteredGroups = new FilteredList<>(groups, p -> true);
            groupsTab= new GroupsTab(this);
            Tables.getTabs().add(groupsTab);
            mainCategories.add(groupsTab);
        }
        groups.add(newGroup);
    }

    private void createAddMainCategory(String mainCategoryName, String categoryName, List<Item> items){
        boolean findMainCategory = mainCategories.parallelStream().anyMatch(mainCategory -> mainCategory.getText().contains(mainCategoryName));
        MainCategory mainCategory;
        if(findMainCategory){
            Tab tab =mainCategories.stream().filter(inMainCategory -> inMainCategory.getText().contains(mainCategoryName)).findFirst().orElse(null);
            if(tab instanceof MainCategory)
                mainCategory= (MainCategory)tab;
            else{
                mainCategory = new MainCategory(mainCategoryName);
                mainCategories.add(mainCategory);
            }
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
        Tables.getTabs().clear();
        Tables.getTabs().setAll(mainCategories);
    }

    public FilteredList<Item> getItemData( String id) {
        return filteredItems.get(id);
    }

    public FilteredList<Group> getFilteredGroups() {
        return filteredGroups;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDataList();
        initializePopup();
    }

    private void initializeDataList() {
        groups = FXCollections.observableArrayList();
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
            if(filteredGroups!=null)
                filteredGroups.setPredicate(group -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if(group.getName().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } else if(group.getTotalCost().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } if(group.getTotalSelling().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } else return group.getNumberOfItemsString().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    public void moveToQuotation(Item newItem, int quantity) {
        mainController.addToInvoice(newItem,quantity);
    }

    public void moveToQuotation(List<Item> newItems, int quantity){
        for (Item item: newItems) {
            mainController.addToInvoice(item,(item.getGroupQuantityInt()*quantity));

        }
    }

    public boolean hasGroups() {
        return (filteredGroups!=null&&filteredGroups.size()!=0);
    }

    public void addToGroup(Item selectedItem, String text) {
        for (Group group:groups) {
            if(group.getName().equals(text)){
                group.addItem(selectedItem);
            }
        }
        saveGroups();
    }

    public List<String> getGroupsName(){
        List<String> names = new ArrayList<>();
        if(groups!=null) {
            for (Group group : groups) {
                names.add(group.getName());
            }
        }
        return names;
    }

    public void removeGroup(Group selectedGroup) {
        groups.remove(selectedGroup);
        if(groups.size()==0){
            mainCategories.remove(groupsTab);
            Tables.getTabs().remove(groupsTab);
            groupsTab=null;
        }
        saveGroups();
    }

    public void saveGroups(){
        mainController.writerGroupToFile(groups.stream().collect(Collectors.toList()));
    }

    public void addGroups(List<Group> groups) {
        for (Group newGroup:groups) {
            addNewGroup(newGroup);
        }
    }
}
