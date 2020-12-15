package quickQuotes.controllers;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import quickQuotes.data.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QuotationController implements Initializable {

    @FXML
    private TableView<Item> TVQuotationTable;

    @FXML
    private TableColumn<Item, String> colQuotationStockCode;

    @FXML
    private TableColumn<Item, String> colQuotationDescription;

    @FXML
    private TableColumn<Item, String> colQuotationQuantity;

    @FXML
    private TableColumn<Item, String> colQuotationUnit;

    @FXML
    private TableColumn<Item, String> colSellingPrice;

    @FXML
    private TableColumn<Item, String> colTotalSellingPrice;

    private InvoiceController invoiceController;
    private Alert popup;
    private HBox popUpBox;
    private String popUpQuantityLblString;
    private Label popUpQuantityLbl;
    private Spinner<Integer> popUpQuantitySpr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePopUp();
    }

    private void initializeTable(){

        colQuotationStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colQuotationDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuotationQuantity.setCellValueFactory(cellData->cellData.getValue().quantityProperty());
        colQuotationUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colSellingPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());
        colTotalSellingPrice.setCellValueFactory(cellData->cellData.getValue().totalSellingPriceProperty());

        colQuotationStockCode.setSortType(TableColumn.SortType.ASCENDING);

        SortedList<Item> sortedData = new SortedList<>(invoiceController.getItemData());

        sortedData.comparatorProperty().bind(TVQuotationTable.comparatorProperty());

        TVQuotationTable.setItems(sortedData);
        TVQuotationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TVQuotationTable.setOnMousePressed(event -> {
          Item selectedItem = TVQuotationTable.getSelectionModel().getSelectedItem();
          if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&selectedItem!=null) {
              popUpQuantityLbl.setText(popUpQuantityLblString.replace("{{0}}",selectedItem.getUnit()));
              popUpQuantitySpr.getValueFactory().setValue(selectedItem.getQuantityInt());
              popUpQuantitySpr.setStyle("");
              popup.show();
          }
        });
    }

    private void initializePopUp() {
        popUpQuantityLblString="Quantity ({{0}}):";
        popUpQuantityLbl = new Label(popUpQuantityLblString);
        popUpQuantitySpr = new Spinner<>(1,9999999,1,1);
        popUpQuantitySpr.setEditable(true);
        popUpQuantitySpr.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;
            String newText =popUpQuantitySpr.getEditor().getText();
            try
            {
                // checking valid integer using parseInt() method
                popUpQuantitySpr.getValueFactory().setValue(Integer.parseInt(newText));
            }
            catch (NumberFormatException e)
            {
                popUpQuantitySpr.setStyle("-fx-border-color: red");
            }
        });
        popUpBox = new HBox(popUpQuantityLbl,popUpQuantitySpr);
        popUpBox.setSpacing(10);
        popup = new Alert(Alert.AlertType.NONE,
                "Item");
        try {
            ((Stage)popup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(SettingsFileController.getLogoPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popup.setTitle("Quick Quotes - Changes Quantity");
        popup.setHeaderText("Changes item");
        popup.getDialogPane().setContent(popUpBox);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = TVQuotationTable.getSelectionModel().getSelectedItem();
                if(selectedItem!=null){
                    selectedItem.setQuantity(popUpQuantitySpr.getValue());
                    invoiceController.updateTotal();
                }
            }
        });
    }

    public List<Item> getSelectionModel() {
        return TVQuotationTable.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
    }

}
