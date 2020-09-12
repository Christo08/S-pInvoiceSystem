package sample.dataReader;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private final StringProperty stockCode;
    private final StringProperty description;
    private final StringProperty costQuantity;
    private final StringProperty sellingQuantity;
    private final StringProperty unit;
    private final StringProperty costPrice;
    private final StringProperty sellingPrice;
    private final StringProperty totalCostPrice;
    private final StringProperty totalSellingPrice;

    private double costQuantityDouble;
    private double sellingQuantityDouble;
    private double costPriceDouble;
    private double sellingPriceDouble;
    private double totalSellingPriceDouble;
    private double totalCostPriceDouble;

    public Item(String stockCode, String description, String unit, double costQuantityDouble, double sellingQuantityDouble, double costPriceDouble, double sellingPriceDouble) {
        this.costQuantityDouble = costQuantityDouble;
        this.sellingQuantityDouble = sellingQuantityDouble;
        this.costPriceDouble = costPriceDouble;
        this.sellingPriceDouble = sellingPriceDouble;
        this.totalSellingPriceDouble = this.sellingQuantityDouble*this.sellingPriceDouble;
        this.totalCostPriceDouble = this.costQuantityDouble*this.costPriceDouble;

        this.stockCode = new SimpleStringProperty(stockCode);
        this.description = new SimpleStringProperty(description);
        this.unit = new SimpleStringProperty(unit);

        this.costQuantity=new SimpleStringProperty(Double.toString(this.costQuantityDouble ));
        this.sellingQuantity=new SimpleStringProperty(Double.toString(this.sellingQuantityDouble ));

        this.costPrice=new SimpleStringProperty(String.format("%.2f", this.costPriceDouble ));
        this.sellingPrice=new SimpleStringProperty(String.format("%.2f", this.sellingPriceDouble ));

        this.totalCostPrice=new SimpleStringProperty(String.format("%.2f", this.totalCostPriceDouble ));
        this.totalSellingPrice=new SimpleStringProperty(String.format("%.2f", this.totalSellingPriceDouble ));
    }

    public String getStockCode() {
        return stockCode.get();
    }

    public StringProperty stockCodeProperty() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode.set(stockCode);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getCostQuantity() {
        return costQuantity.get();
    }

    public StringProperty costQuantityProperty() {
        return costQuantity;
    }

    public void setCostQuantity(double costQuantity) {
        this.costPriceDouble=costQuantity;
        recalculateTotalCostPrice();
        this.costQuantity.set(Double.toString(this.costQuantityDouble ));
    }

    public String getSellingQuantity() {
        return sellingQuantity.get();
    }

    public StringProperty sellingQuantityProperty() {
        return sellingQuantity;
    }

    public void setSellingQuantity(double sellingQuantity) {
        this.sellingQuantityDouble=sellingQuantity;
        recalculateTotalSellingPrice();
        this.sellingQuantity.set(Double.toString(this.sellingQuantityDouble ));
    }

    public String getUnit() {
        return unit.get();
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public String getCostPrice() {
        return costPrice.get();
    }

    public StringProperty costPriceProperty() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPriceDouble =costPrice;
        recalculateTotalCostPrice();
        this.costPrice.set(String.format("%.2f", this.costPriceDouble ));
    }

    public String getSellingPrice() {
        return sellingPrice.get();
    }

    public StringProperty sellingPriceProperty() {
        return sellingPrice;
    }

    public String getTotalSellingPrice() {
        return totalSellingPrice.get();
    }

    public StringProperty totalSellingPriceProperty() {
        return totalSellingPrice;
    }

    public String getTotalCostPrice() {
        return this.totalCostPrice.get();
    }

    public StringProperty totalCostPriceProperty() {
        return this.totalCostPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPriceDouble=sellingPrice;
        recalculateTotalSellingPrice();
        this.sellingPrice.set(String.format("%.2f", this.sellingPriceDouble ));
    }

    public double getCostQuantityDouble() {
        return costQuantityDouble;
    }

    public double getSellingQuantityDouble() {
        return sellingQuantityDouble;
    }

    public double getCostPriceDouble() {
        return costPriceDouble;
    }

    public double getSellingPriceDouble() {
        return sellingPriceDouble;
    }

    public double getTotalSellingPriceDouble() {
        return totalSellingPriceDouble;
    }

    public double getTotalCostPriceDouble() {
        return totalCostPriceDouble;
    }

    private void recalculateTotalCostPrice(){
        this.totalCostPriceDouble = this.costQuantityDouble*this.costPriceDouble;
        this.totalCostPrice.setValue(String.format("%.2f", this.totalCostPriceDouble ));
    }

    private void recalculateTotalSellingPrice(){
        this.totalSellingPriceDouble = this.sellingQuantityDouble*this.sellingPriceDouble;
        this.totalSellingPrice.setValue(String.format("%.2f", this.totalSellingPriceDouble ));
    }
}
