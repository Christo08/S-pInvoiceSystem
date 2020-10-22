package quickQuotes.data;

import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import quickQuotes.controllers.InvoiceController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class CostingSheet extends Tab {
    TableView<Item> table;
    private TableColumn<Item, String> colStockCode;
    private TableColumn<Item, String> colDescription;
    private TableColumn<Item, String> colQuantity;
    private TableColumn<Item, String> colUnit;
    private TableColumn<Item, String> colProfit;
    private TableColumn<Item, String> colCostingPrice;
    private TableColumn<Item, String> colTotalCostingPrice;

    private InvoiceController invoiceController;

    private Alert popup;
    private GridPane popUpPane;
    private String popUpQuantityLblString;
    private Label popUpQuantityLbl;
    private Spinner<Integer> popUpQuantitySpr;
    private Label popUpProfitLbl;
    private Spinner<Double> popUpProfitSpr;

    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String logoName = "Logo.PNG";
    String absoluteLogoPath = recoursePath +"\\"+ logoName;

    public CostingSheet(InvoiceController invoiceController){
        this.invoiceController=invoiceController;

        table = new TableView<>();
        table.setEditable(false);

        colStockCode = new TableColumn<>("Stock Code");
        colDescription = new TableColumn<>("Description");
        colQuantity = new TableColumn<>("Quantity");
        colUnit = new TableColumn<>("Unit");
        colProfit = new TableColumn<>("Profit (%)");
        colCostingPrice = new TableColumn<>("Costing Price");
        colTotalCostingPrice = new TableColumn<>("Total Costing Price");

        colStockCode.setCellValueFactory(cellData -> cellData.getValue().stockCodeProperty());
        colDescription.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
        colQuantity.setCellValueFactory(cellData->cellData.getValue().quantityProperty());
        colUnit.setCellValueFactory(cellData->cellData.getValue().unitProperty());
        colProfit.setCellValueFactory(cellData->cellData.getValue().profitPercentProperty());
        colCostingPrice.setCellValueFactory(cellData->cellData.getValue().costPriceProperty());
        colTotalCostingPrice.setCellValueFactory(cellData->cellData.getValue().totalCostPriceProperty());

        table.getColumns().addAll(colStockCode,colDescription,colQuantity,colUnit,colProfit,colCostingPrice,colTotalCostingPrice);

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
        this.setId("Costing sheet");
        this.setText("Costing sheet");
        this.setContent(table);
        this.setClosable(false);
    }

    private void initializePopup(){
        popUpProfitLbl = new Label("Profit Percent(%):");
        popUpProfitSpr = new Spinner<>(15.0,100.0,25.0,1.0);
        popUpProfitSpr.setEditable(true);
        popUpProfitSpr.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;
            String newText =popUpProfitSpr.getEditor().getText();
            try
            {
                // checking valid integer using parseInt() method
                double newNumber = Double.parseDouble(newText);
                if(newNumber>=15&&newNumber<=100){
                    popUpProfitSpr.getValueFactory().setValue(newNumber);
                }else{
                    popUpProfitSpr.setStyle("-fx-border-color: red");
                }
            }
            catch (NumberFormatException e)
            {
                popUpProfitSpr.setStyle("-fx-border-color: red");
            }
        });

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
                int newNumber = Integer.parseInt(newText);
                if(newNumber>=1&&newNumber<=9999999){
                    popUpQuantitySpr.getValueFactory().setValue(newNumber);
                }else{
                    popUpQuantitySpr.setStyle("-fx-border-color: red");
                }
            }
            catch (NumberFormatException e)
            {
                popUpQuantitySpr.setStyle("-fx-border-color: red");
            }
        });

        popUpPane = new GridPane();
        popUpPane.setAlignment(Pos.CENTER);
        popUpPane.setVgap(10);
        popUpPane.setHgap(10);
        popUpPane.add(popUpProfitLbl,0,0);
        popUpPane.add(popUpProfitSpr,1,0);
        popUpPane.add(popUpQuantityLbl,0,1);
        popUpPane.add(popUpQuantitySpr,1,1);
        popup = new Alert(Alert.AlertType.NONE,"Item");
        popup.setTitle("Quick Quotes - Changes Item");
        try {
            ((Stage)popup.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new FileInputStream(absoluteLogoPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        popup.setHeaderText("Changes item");
        popup.getDialogPane().setContent(popUpPane);
        popup.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
        popup.setOnHidden(e -> {
            if (popup.getResult() == ButtonType.APPLY) {
                Item selectedItem = table.getSelectionModel().getSelectedItem();
                if(selectedItem!=null){
                    selectedItem.setProfitPercent(popUpProfitSpr.getValue());
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
