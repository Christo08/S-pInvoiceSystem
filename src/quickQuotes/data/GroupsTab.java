package quickQuotes.data;

import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quickQuotes.controllers.CategoriesController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class GroupsTab extends Tab {
    private TableView<Group> table;
    private TableColumn<Group, String> colName;
    private TableColumn<Group, String> colNumberOfItems;
    private TableColumn<Group, String> colTotalCost;
    private TableColumn<Group, String> colTotalSelling;
    private CategoriesController categoriesController;
    private ContextMenu contextMenu;
    private Alert popupQuotation;
    private Alert popupRemove;
    private Alert popupGroupView;
    private int hashOfSelectedGroup;

    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;

    public GroupsTab(CategoriesController categoriesController) {
        initializePopup();
        initializeContextMenu();
        table = new TableView<>();
        table.setEditable(false);
        colName = new TableColumn<>("Group Name");
        colNumberOfItems = new TableColumn<>("Number Of Items");
        colTotalCost = new TableColumn<>("Total Cost (R)");
        colTotalSelling = new TableColumn<>("Total Selling (R)");

        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colNumberOfItems.setCellValueFactory(cellData->cellData.getValue().numberOfItemsProperty());
        colTotalCost.setCellValueFactory(cellData->cellData.getValue().totalCostProperty());
        colTotalSelling.setCellValueFactory(cellData->cellData.getValue().totalSellingProperty());

        table.getColumns().addAll(colName, colNumberOfItems, colTotalCost,colTotalSelling);

        SortedList<Group> sortedData = new SortedList<>(categoriesController.getFilteredGroups());

        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.setOnMousePressed(event -> {
            Group selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                contextMenu.hide();
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    popupGroupView.showAndWait();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(table, event.getScreenX(), event.getScreenY());
                }
            }
        });

        this.categoriesController=categoriesController;
        this.setText("Groups");
        this.setClosable(false);
        this.setContent(table);
    }

    private void initializePopup() {
        Label popUpQuotationLbl = new Label("Number of items to add to the quotation:");
        Spinner<Integer> popUpQuotationSpr = new Spinner<>(1, 10000, 1, 1);
        HBox quotationHBox = new HBox(popUpQuotationLbl, popUpQuotationSpr);
        quotationHBox.setSpacing(10);
        popupQuotation = new Alert(Alert.AlertType.NONE);
        popupQuotation.setTitle("Quick Quotes - Add Item");
        try {
            ((Stage) popupQuotation.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupQuotation.setHeaderText("Add item");
        popupQuotation.getDialogPane().setContent(quotationHBox);
        popupQuotation.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupQuotation.setOnHidden(e -> {
            if (popupQuotation.getResult() == ButtonType.APPLY) {
                Group selectedGroup = table.getSelectionModel().getSelectedItem();
                if (selectedGroup != null) {
                    categoriesController.moveToQuotation(selectedGroup.getItemList(),popUpQuotationSpr.getValue());
                }
            }
        });

        popupRemove = new Alert(Alert.AlertType.CONFIRMATION);
        popupRemove.setTitle("Quick Quotes - Remove group");
        try {
            ((Stage) popupRemove.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupRemove.setContentText("Are you sure you want to remove the group?");
        popupRemove.setOnHidden(e -> {
            if (popupRemove.getResult() == ButtonType.OK) {
                Group selectedGroup = table.getSelectionModel().getSelectedItem();
                if (selectedGroup != null) {
                    categoriesController.removeGroup(selectedGroup);
                }
            }
        });

        VBox groupVBox = new VBox();
        groupVBox.setSpacing(10);
        HBox groupNameRowHBox = new HBox();
        groupNameRowHBox.setSpacing(10);
        Label groupNameLbl = new Label("Group name");
        TextField groupNameTxt = new TextField();
        groupNameRowHBox.getChildren().addAll(groupNameLbl,groupNameTxt);
        groupVBox.getChildren().addAll(groupNameRowHBox);
        popupGroupView = new Alert(Alert.AlertType.NONE);
        popupGroupView.setTitle("Quick Quotes - Group");
        try {
            ((Stage) popupGroupView.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popupGroupView.getDialogPane().setContent(groupVBox);
        popupGroupView.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popupGroupView.setOnShown(e->{
            Group group = table.getSelectionModel().getSelectedItem();
            groupNameTxt.setText(group.getName());
            if(groupVBox.getChildren().size()>1&&groupVBox.getChildren().get(1)!=null){
                groupVBox.getChildren().remove(1);
            }
            groupVBox.getChildren().add(group.getTable());
            groupVBox.setMinWidth(525.0);
            hashOfSelectedGroup=group.hashCode();
        });
        popupGroupView.setOnHidden(e -> {
            if (popupGroupView.getResult() == ButtonType.APPLY) {
                Group selectedGroup = table.getSelectionModel().getSelectedItem();
                boolean nameCheck = table.getItems().stream().anyMatch(group -> group.getName().equals(groupNameTxt.getText()));
                if (selectedGroup != null&&!groupNameTxt.getText().isEmpty() &&!nameCheck) {
                    selectedGroup.setName(groupNameTxt.getText());
                    categoriesController.saveGroups();
                }
                else if (groupNameTxt.getText().isEmpty()) {
                    Alert popupError = new Alert(Alert.AlertType.WARNING);
                    popupError.setTitle("Quick Quotes - Group error");
                    try {
                        ((Stage) popupError.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    popupError.setContentText("Please enter a name.");
                    popupError.showAndWait();
                }else if(nameCheck && !selectedGroup.getName().equals(groupNameTxt.getText())){
                    Alert popupError = new Alert(Alert.AlertType.WARNING);
                    popupError.setTitle("Quick Quotes - Group error");
                    try {
                        ((Stage) popupError.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    popupError.setContentText("Please enter a valid name.");
                    popupError.showAndWait();
                }else if(hashOfSelectedGroup!=selectedGroup.hashCode()){
                    categoriesController.saveGroups();
                }
            }
        });
    }

    private void initializeContextMenu(){
        contextMenu = new ContextMenu();
        MenuItem viewGroupMenuItem = new MenuItem("View Group");
        viewGroupMenuItem.setOnAction(event -> {
            popupGroupView.showAndWait();
        });
        contextMenu.getItems().add(viewGroupMenuItem);
        MenuItem addToQuotationMenuItem = new MenuItem("Add To quotation");
        addToQuotationMenuItem.setOnAction(event -> {
            popupQuotation.showAndWait();
        });
        contextMenu.getItems().add(addToQuotationMenuItem);
        MenuItem removeGroupMenuItem = new MenuItem("Remove group");
        removeGroupMenuItem.setOnAction(event -> {
            popupRemove.showAndWait();
        });
        contextMenu.getItems().add(removeGroupMenuItem);
    }

}
