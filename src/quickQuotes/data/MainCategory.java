package quickQuotes.data;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainCategory extends Tab {
    private TabPane tabPane;
    private List<Category> tabs;

    public MainCategory(String name){
        tabPane = new TabPane();
        tabs = new ArrayList<>();
        this.setText(name);
        this.setClosable(false);
        this.setContent(tabPane);
    }

    public void addSubCategory(Category category){
        tabPane.getTabs().add(category);
        tabs.add(category);
        sortTabs();
    }

    private void sortTabs() {
        tabs.sort(Comparator.comparing(Tab::getText));
        tabPane.getTabs().clear();
        tabPane.getTabs().setAll(tabs);
    }

    public List<Item> getSelectedItems() {
        int index =tabPane.getSelectionModel().getSelectedIndex();
        return tabs.get(index).getSelectedItems();
    }

    public void removeTabs() {
        tabPane.getTabs().clear();
        tabs = new ArrayList<>();
    }

    public String getSelectedName() {
        return tabPane.getSelectionModel().getSelectedItem().getText();
    }
}
