package quickQuotes.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private List<Item> itemList;
    private String nameString;
    private double totalCostDouble;
    private double totalSellingDouble;
    private int numberOfItemsInt;

    private final StringProperty name;
    private final StringProperty totalCost;
    private final StringProperty totalSelling;
    private final StringProperty numberOfItems;

    public Group(String name) {
        this.nameString = name;
        this.totalCostDouble=0.0;
        this.totalSellingDouble=0.0;
        this.numberOfItemsInt=0;
        this.itemList=new ArrayList<>();

        this.name = new SimpleStringProperty(name);
        this.totalCost = new SimpleStringProperty(String.format("%.2f", this.totalCostDouble));
        this.totalSelling = new SimpleStringProperty(String.format("%.2f", this.totalSellingDouble));
        this.numberOfItems = new SimpleStringProperty((Integer.toString(this.numberOfItemsInt)));
    }

    public void addItem(Item newItem){
        numberOfItemsInt++;
        totalCostDouble+=newItem.getCostPriceDouble();
        totalSellingDouble+=newItem.getSellingPriceDouble();
        itemList.add(newItem);
        referenceStringProperty();
    }

    public void removeItem(Item newItem){
        numberOfItemsInt--;
        totalCostDouble-=newItem.getCostPriceDouble();
        totalSellingDouble-=newItem.getSellingPriceDouble();
        itemList.remove(newItem);
        referenceStringProperty();
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public double getTotalCostDouble() {
        return totalCostDouble;
    }

    public void setTotalCostDouble(double totalCostDouble) {
        this.totalCostDouble = totalCostDouble;
    }

    public double getTotalSellingDouble() {
        return totalSellingDouble;
    }

    public void setTotalSellingDouble(double totalSellingDouble) {
        this.totalSellingDouble = totalSellingDouble;
    }

    public int getNumberOfItemsInt() {
        return numberOfItemsInt;
    }

    public void setNumberOfItemsInt(int numberOfItemsInt) {
        this.numberOfItemsInt = numberOfItemsInt;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getTotalCost() {
        return totalCost.get();
    }

    public StringProperty totalCostProperty() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost.set(totalCost);
    }

    public String getTotalSelling() {
        return totalSelling.get();
    }

    public StringProperty totalSellingProperty() {
        return totalSelling;
    }

    public void setTotalSelling(String totalSelling) {
        this.totalSelling.set(totalSelling);
    }

    public String getNumberOfItems() {
        return numberOfItems.get();
    }

    public StringProperty numberOfItemsProperty() {
        return numberOfItems;
    }

    public void setNumberOfItems(String numberOfItems) {
        this.numberOfItems.set(numberOfItems);
    }

    private void referenceStringProperty(){
        this.totalCost.setValue(String.format("%.2f", this.totalCostDouble));
        this.totalSelling.setValue(String.format("%.2f", this.totalSellingDouble));
        this.numberOfItems.setValue((Integer.toString(this.numberOfItemsInt)));
    }
}
