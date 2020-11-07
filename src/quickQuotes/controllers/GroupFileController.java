package quickQuotes.controllers;

import quickQuotes.data.Group;
import quickQuotes.data.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GroupFileController {
    String fileName = "groups.txt";
    public static String recoursePath = new File("src/quickQuotes/resource/").getAbsolutePath();
    String absolutePath = recoursePath +"/"+fileName;

    List<Group> groups;

    public GroupFileController(List<Item> items) {
        groups = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(absolutePath))) {
            String line = bufferedReader.readLine();
            while(line != null) {
                String[] lineSplit = line.split(" , ");
                String name =lineSplit[0].trim();
                Group newGroup = new Group(name);
                String tempString =lineSplit[1].substring(1,lineSplit[1].length()-1);
                String[] itemsNameAndQuantity = tempString.split(", ");
                if(!itemsNameAndQuantity[0].isEmpty())
                {
                    for (int counter = 0; counter < itemsNameAndQuantity.length; counter++) {
                        String[] itemInfo = (itemsNameAndQuantity[counter].substring(1, itemsNameAndQuantity[counter].length() - 1)).split(",");
                        Item item = items.stream().filter(item1 -> item1.getStockCode().contentEquals(itemInfo[0])).findFirst().orElse(null);
                        int numberOfGroup = Integer.parseInt(itemInfo[1]);
                        item.setGroupQuantity(numberOfGroup);
                        newGroup.addItem(item);
                    }
                }
                groups.add(newGroup);
                line = bufferedReader.readLine();
            }
        }catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void saveGroups(List<Group> groups){
        try {
            FileWriter file = new FileWriter(absolutePath);
            BufferedWriter output = new BufferedWriter(file);
            String data = formatData(groups);
            output.write(data);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatData(List<Group> groups){
        String outputString="";
        for(Group group:groups){
            outputString+=group.toString()+"\n";
        }
        return outputString;
    }
}
