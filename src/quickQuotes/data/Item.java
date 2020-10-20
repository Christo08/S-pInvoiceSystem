package quickQuotes.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Item {
    private final StringProperty stockCode;
    private final StringProperty description;
    private final StringProperty quantity;
    private final StringProperty unit;
    private final StringProperty profitPercent;
    private final StringProperty costPrice;
    private final StringProperty sellingPrice;
    private final StringProperty totalCostPrice;
    private final StringProperty totalSellingPrice;

    private int quantityInt;
    private double profitPercentDouble;
    private double costPriceDouble;
    private double sellingPriceDouble;
    private double totalCostPriceDouble;
    private double totalSellingPriceDouble;

    public  Item(Item originalItem){
        this(originalItem.getStockCode(),originalItem.getDescription(),originalItem.getUnit(), originalItem.getProfitPercentDouble(), originalItem.getCostPriceDouble());
    }

    public Item(String stockCode, String description, String unit,  double costPrice) {
        this(stockCode, description, 0, unit,  costPrice);
    }

    public Item(String stockCode, String description, String unit, double profitPercent,  double costPrice) {
        this(stockCode, description, 0, unit,profitPercent,  costPrice);
    }

    public Item(String stockCode, String description, int quantity, String unit,  double costPrice) {
        this(stockCode, description, quantity, unit, 25, costPrice);
    }

    public Item(String stockCode, String description, int quantity, String unit, double profitPercent, double costPrice) {
        this.quantityInt=quantity;
        this.profitPercentDouble=profitPercent;
        this.costPriceDouble=costPrice;
        this.sellingPriceDouble=(this.costPriceDouble/(100-this.profitPercentDouble))*100;
        this.totalCostPriceDouble=this.costPriceDouble*this.quantityInt;
        this.totalSellingPriceDouble=this.sellingPriceDouble*this.quantityInt;

        if(unit.equalsIgnoreCase("mete")){
            unit="m";
        }

        this.stockCode = new SimpleStringProperty(stockCode);
        this.description = new SimpleStringProperty(description);
        this.quantity = new SimpleStringProperty(Integer.toString(this.quantityInt));
        this.unit = new SimpleStringProperty(unit);
        this.profitPercent = new SimpleStringProperty(String.format("%.2f", this.profitPercentDouble));
        this.costPrice = new SimpleStringProperty(String.format("%.2f", this.costPriceDouble));
        this.sellingPrice = new SimpleStringProperty(String.format("%.2f", this.sellingPriceDouble));
        this.totalCostPrice = new SimpleStringProperty(String.format("%.2f", this.totalCostPriceDouble));
        this.totalSellingPrice = new SimpleStringProperty(String.format("%.2f", this.totalSellingPriceDouble));
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

    public String getQuantity() {
        return quantity.get();
    }

    public StringProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantityInt = quantity;
        recalculateTotals();
        this.quantity.set(Integer.toString(this.quantityInt));
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

    public String getProfitPercent() {
        return profitPercent.get();
    }

    public StringProperty profitPercentProperty() {
        return profitPercent;
    }

    public void setProfitPercent(double profitPercent) {
        this.profitPercentDouble=profitPercent;
        recalculateTotals();
        this.profitPercent.set(String.format("%.2f", this.profitPercentDouble));
    }

    public String getCostPrice() {
        return costPrice.get();
    }

    public StringProperty costPriceProperty() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPriceDouble=costPrice;
        recalculateTotals();
        this.costPrice.set(String.format("%.2f", this.costPriceDouble));
    }

    public String getSellingPrice() {
        return sellingPrice.get();
    }

    public StringProperty sellingPriceProperty() {
        return sellingPrice;
    }

    public String getTotalCostPrice() {
        return totalCostPrice.get();
    }

    public StringProperty totalCostPriceProperty() {
        return totalCostPrice;
    }

    public String getTotalSellingPrice() {
        return totalSellingPrice.get();
    }

    public StringProperty totalSellingPriceProperty() {
        return totalSellingPrice;
    }

    public int getQuantityInt() {
        return quantityInt;
    }

    public double getProfitPercentDouble() {
        return profitPercentDouble;
    }

    public double getCostPriceDouble() {
        return costPriceDouble;
    }

    public double getSellingPriceDouble() {
        return sellingPriceDouble;
    }

    public double getTotalCostPriceDouble() {
        return totalCostPriceDouble;
    }

    public double getTotalSellingPriceDouble() {
        return totalSellingPriceDouble;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.profitPercentDouble, profitPercentDouble) == 0 &&
                Double.compare(item.costPriceDouble, costPriceDouble) == 0 &&
                Objects.equals(stockCode, item.stockCode) &&
                Objects.equals(description, item.description) &&
                Objects.equals(unit, item.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockCode, description, unit, profitPercentDouble, costPriceDouble);
    }

    @Override
    public String toString() {
        return "Item{" +
                "stockCode=" + getStockCode() +
                ", description=" + getDescription() +
                ", quantity=" + getQuantity() +
                ", unit=" + getUnit() +
                ", profitPercent=" + getProfitPercent() +
                ", costPrice=" + getCostPrice() +
                ", sellingPrice=" + getSellingPrice() +
                ", totalCostPrice=" + getTotalCostPrice() +
                ", totalSellingPrice=" + getTotalSellingPrice() +
                ", quantityInt=" + quantityInt +
                ", profitPercentDouble=" + profitPercentDouble +
                ", costPriceDouble=" + costPriceDouble +
                ", sellingPriceDouble=" + sellingPriceDouble +
                ", totalCostPriceDouble=" + totalCostPriceDouble +
                ", totalSellingPriceDouble=" + totalSellingPriceDouble +
                '}';
    }

    private void recalculateTotals() {
        this.sellingPriceDouble=(this.costPriceDouble/(100-this.profitPercentDouble))*100;
        this.totalCostPriceDouble=this.costPriceDouble*this.quantityInt;
        this.totalSellingPriceDouble=this.sellingPriceDouble*this.quantityInt;
        this.sellingPrice.set(String.format("%.2f", this.sellingPriceDouble));
        this.totalCostPrice.set(String.format("%.2f", this.totalCostPriceDouble));
        this.totalSellingPrice.set(String.format("%.2f", this.totalSellingPriceDouble));

    }
}
