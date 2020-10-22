package quickQuotes.data;

import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import quickQuotes.controllers.InvoiceController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class Quotation extends Tab {
    TableView<Item> table;
    private TableColumn<Item, String> colStockCode;
    private TableColumn<Item, String> colDescription;
    private TableColumn<Item, String> colQuantity;
    private TableColumn<Item, String> colUnit;
    private TableColumn<Item, String> colSellingPrice;
    private TableColumn<Item, String> colTotalSellingPrice;

    private Alert popup;
    private HBox popUpBox;
    private String popUpQuantityLblString;
    private Label popUpQuantityLbl;
    private Spinner<Integer> popUpQuantitySpr;
    private InvoiceController invoiceController;

    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;

    public Quotation(InvoiceController invoiceController){
        this.invoiceController=invoiceController;

        table = new TableView<>();
        table.setEditable(false);

        colStockCode = new TableColumn<>("Stock Code");
        colDescription = new TableColumn<>("Description");
        colQuantity = new TableColumn<>("Quantity");
        colUnit = new TableColumn<>("Unit");
        colSellingPrice = new TableColumn<>("Selling Price");
        colTotalSellingPrice = new TableColumn<>("Total Selling Price");

        colStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuantity.setCellValueFactory(cellData->cellData.getValue().quantityProperty());
        colUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colSellingPrice.setCellValueFactory(cellData->cellData.getValue().sellingPriceProperty());
        colTotalSellingPrice.setCellValueFactory(cellData->cellData.getValue().totalSellingPriceProperty());

        table.getColumns().addAll(colStockCode,colDescription,colQuantity,colUnit,colSellingPrice,colTotalSellingPrice);

        colStockCode.setSortType(TableColumn.SortType.ASCENDING);

        SortedList<Item> sortedData= new SortedList<Item>(this.invoiceController.getItemData());

        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.setOnMousePressed(event -> {
            Item selectedItem = table.getSelectionModel().getSelectedItem();
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2&&selectedItem!=null) {
                popUpQuantityLbl.setText(popUpQuantityLblString.replace("{{0}}",selectedItem.getUnit()));
                popUpQuantitySpr.getValueFactory().setValue(selectedItem.getQuantityInt());
                popUpQuantitySpr.setStyle("");
                popup.show();
            }
        });

        initializePopup();

        this.invoiceController=invoiceController;
        this.setId("Quotation");
        this.setText("Quotation");
        this.setContent(table);
        this.setClosable(false);
    }

    private void initializePopup(){
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
            ((Stage)popup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popup.setTitle("Quick Quotes - Changes Quantity");
        popup.setHeaderText("Changes item");
        popup.getDialogPane().setContent(popUpBox);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = table.getSelectionModel().getSelectedItem();
                if(selectedItem!=null){
                    selectedItem.setQuantity(popUpQuantitySpr.getValue());
                    invoiceController.updateTotal();
                }
            }
        });
    }

    public List<Item> getSelectionModel() {
        return table.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
    }
}
