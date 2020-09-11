package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sample.dataReader.Item;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private InvoiceController invoiceController=new InvoiceController();


    public void addToInvoice(Item newItem){
        invoiceController.add(newItem);
    }

    public void addToInvoice(List<Item> newItems){
        for (Item newItem: newItems) {
            invoiceController.add(newItem);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
