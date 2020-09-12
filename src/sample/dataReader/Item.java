package sample.dataReader;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Item {
    private final StringProperty name;
    private final StringProperty numderOfItems;
    private final StringProperty units;
    private final StringProperty price;
    private final StringProperty totalePrice;

    private double numberOfItemsDoulbe;
    private double priceDoulbe;

    private double totalePriceDoulbe;

    public Item(String name, double numberOfItems, String units, double price) {
        numberOfItemsDoulbe=numberOfItems;
        priceDoulbe =price;
        totalePriceDoulbe = price*numberOfItems;

        this.name = new SimpleStringProperty(name);
        this.numderOfItems = new SimpleStringProperty(Double.toString(numberOfItems));
        this.units = new SimpleStringProperty(units);
        this.price = new SimpleStringProperty(String.format("%.2f", price));
        this.totalePrice = new SimpleStringProperty(String.format("%.2f", totalePriceDoulbe));
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setNumberOfItems(double numberOfItems) {
        numberOfItemsDoulbe=numberOfItems;
        recalculotTotalPrice();
        this.numderOfItems.set(Double.toString(numberOfItems));
    }

    public void setUnits(String units) {
        this.units.set(units);
    }

    public void setPrice(double price) {
        priceDoulbe =price;
        recalculotTotalPrice();
        this.price.set(String.format("%.2f", price));
    }

    public String getName() {
        return name.get();
    }

    public String getNumberOfItems() {
        return numderOfItems.get();
    }

    public String getUnits() {
        return units.get();
    }

    public String getPrice() {
        return price.get();
    }

    public String getTotalePrice() {
        return totalePrice.get();
    }

    public double getNumberOfItemsDoulbe() {
        return numberOfItemsDoulbe;
    }

    public double getPriceDoulbe() {
        return priceDoulbe;
    }

    public double getTotalePriceDoulbe() {
        return totalePriceDoulbe;
    }

    public void recalculotTotalPrice(){
        totalePriceDoulbe = priceDoulbe*numberOfItemsDoulbe;
        this.totalePrice.set(String.format("%.2f", totalePriceDoulbe));
    }

    public ObservableValue<String> getNameProperty() {
        return this.name;
    }

    public ObservableValue<String> getNumberOfItemsProperty() {
        return this.numderOfItems;
    }

    public ObservableValue<String> getUnitsProperty() {
        return this.units;
    }

    public ObservableValue<String> getPriceProperty() {
        return this.price;
    }

    public ObservableValue<String> getTotalePriceProperty() {
        return this.totalePrice;
    }

    @Override
    public String toString(){
        return "Name: "+getName()+" number of items: "+getNumberOfItems()+ " Units: "+getUnits()+" Price for one: "+getPrice()+ " Totale price: "+getTotalePrice();
    }
}
