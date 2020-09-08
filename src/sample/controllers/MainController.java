package sample.controllers;

import javafx.fxml.FXML;
import sample.dataReader.Item;

import java.util.List;

public class MainController {
    @FXML
    private InvoiceController invoiceController;

    @FXML
    private void initialize(){
        invoiceController.initialize(this);
    }

    public void addToInvoice(Item newItem){
        invoiceController.add(newItem);
    }

    public void addToInvoice(List<Item> newItems){
        for (Item newItem: newItems) {
            invoiceController.add(newItem);
        }
    }

}
