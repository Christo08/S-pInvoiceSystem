package sample.dataReader;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Item {
    private final StringProperty name;
    private final DoubleProperty numderOfItems;
    private final StringProperty units;
    private final DoubleProperty price;
    private final DoubleProperty totalePrice;

    public Item(String name, double numderOfItems, String units, double price) {
        this.name = new SimpleStringProperty(name);
        this.numderOfItems = new SimpleDoubleProperty(numderOfItems);
        this.units = new SimpleStringProperty(units);
        this.price = new SimpleDoubleProperty(price);
        this.totalePrice = new SimpleDoubleProperty(numderOfItems*price);
    }


    public void setName(String name) {
        this.name.set(name);
    }

    public void setNumderOfItems(double numderOfItems) {
        this.numderOfItems.set(numderOfItems);
    }

    public void setUnits(String units) {
        this.units.set(units);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public String getName() {
        return name.get();
    }

    public double getNumderOfItems() {
        return numderOfItems.get();
    }

    public String getUnits() {
        return units.get();
    }

    public double getPrice() {
        return price.get();
    }

    public double getTotalePrice() {
        return totalePrice.get();
    }

    public void recalculotTotalPrice(){
        this.totalePrice.set(numderOfItems.get()*price.get());
    }

    public ObservableValue<String> getNameProperty() {
        return this.name;
    }


    public ObservableValue<String> getUnitsProperty() {
        return this.units;
    }
}
