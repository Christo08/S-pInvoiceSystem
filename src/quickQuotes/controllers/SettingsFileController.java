package quickQuotes.controllers;

import com.google.common.collect.Lists;
import quickQuotes.data.User;
import java.io.*;
import java.util.*;

public class SettingsFileController {
    static private String absolutePath;
    static private Map<String,Object> keyValuePair;

    static public void initSettingsFileController(String path) throws Exception {
        absolutePath=path;
        keyValuePair = new HashMap<>();
        //read data
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(absolutePath))) {
            String line = bufferedReader.readLine();
            while(line != null) {
                if(!line.isEmpty() && line.charAt(0)!='#'){
                   String[] lineSplit = line.split("=");
                   if(lineSplit[1].trim().contentEquals("true")){
                       keyValuePair.put(lineSplit[0],true);
                   }else if(lineSplit[1].trim().contentEquals("false")){
                       keyValuePair.put(lineSplit[0],false);
                   }else if(lineSplit[1].charAt(0)=='['&&lineSplit[1].trim().charAt(lineSplit[1].trim().length()-1)==']'){
                       if(lineSplit[0].equals("UsersTab.Data.Users")){
                           if(lineSplit[1].trim().equalsIgnoreCase("[]")){
                               keyValuePair.put(lineSplit[0], new ArrayList<>());
                           }else {
                               String[] stringUsers = lineSplit[1].substring(1, lineSplit[1].length() - 1).split(", ");
                               List<User> users = new ArrayList<>();
                               for (int counter = 0; counter < stringUsers.length; counter++) {
                                   users.add(new User(stringUsers[counter].trim()));
                               }
                               keyValuePair.put(lineSplit[0], users);
                           }
                       }
                   }else if(lineSplit[0].contains("UsersCounter")||lineSplit[0].contains("Position")) {
                       keyValuePair.put(lineSplit[0],Integer.parseInt(lineSplit[1].trim()));
                   }else{
                       keyValuePair.put(lineSplit[0],lineSplit[1].trim());
                   }
                }
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            System.out.println("Can not open the settings file");
        }
    }

    static public Map<String, Integer> getQuotationPositions(){
        Map<String,Integer> results = new HashMap<>();
        if (keyValuePair.containsKey("PDFTab.Data.Quotation.Position.info"))
            results.put("PDFTab.Data.Quotation.Position.info",  (int)keyValuePair.get("PDFTab.Data.Quotation.Position.info"));
        else
            results.put("PDFTab.Data.Quotation.Position.info",  -1);
        if (keyValuePair.containsKey("PDFTab.Data.Quotation.Position.table"))
            results.put("PDFTab.Data.Quotation.Position.table",  (int)keyValuePair.get("PDFTab.Data.Quotation.Position.table"));
        else
            results.put("PDFTab.Data.Quotation.Position.table",  -1);
        if (keyValuePair.containsKey("PDFTab.Data.Quotation.Position.text"))
            results.put("PDFTab.Data.Quotation.Position.text",  (int)keyValuePair.get("PDFTab.Data.Quotation.Position.text"));
        else
            results.put("PDFTab.Data.Quotation.Position.text",  -1);
        return results;
    }

    static public Map<String, Integer> getCostingPositions(){
        Map<String,Integer> results = new HashMap<>();
        if (keyValuePair.containsKey("PDFTab.Data.CostingSheet.Position.info"))
            results.put("PDFTab.Data.CostingSheet.Position.info",  (int)keyValuePair.get("PDFTab.Data.CostingSheet.Position.info"));
        else
            results.put("PDFTab.Data.CostingSheet.Position.info",  -1);
        if (keyValuePair.containsKey("PDFTab.Data.CostingSheet.Position.table"))
            results.put("PDFTab.Data.CostingSheet.Position.table",  (int)keyValuePair.get("PDFTab.Data.CostingSheet.Position.table"));
        else
            results.put("PDFTab.Data.CostingSheet.Position.table",  -1);
        if (keyValuePair.containsKey("PDFTab.Data.CostingSheet.Position.text"))
            results.put("PDFTab.Data.CostingSheet.Position.text",  (int)keyValuePair.get("PDFTab.Data.CostingSheet.Position.text"));
        else
            results.put("PDFTab.Data.CostingSheet.Position.text", -1);
        return results;
    }

    static public Map<String, Integer> getCheckingPositions(){
        Map<String,Integer> results = new HashMap<>();
        if (keyValuePair.containsKey("PDFTab.Data.CheckingSheet.Position.info"))
            results.put("PDFTab.Data.CheckingSheet.Position.info",  (int)keyValuePair.get("PDFTab.Data.CheckingSheet.Position.info"));
        else
            results.put("PDFTab.Data.CheckingSheet.Position.info",  -1);
        if (keyValuePair.containsKey("PDFTab.Data.CheckingSheet.Position.table"))
            results.put("PDFTab.Data.CheckingSheet.Position.table",  (int)keyValuePair.get("PDFTab.Data.CheckingSheet.Position.table"));
        else
            results.put("PDFTab.Data.CheckingSheet.Position.table",  -1);
        if (keyValuePair.containsKey("PDFTab.Data.CheckingSheet.Position.text"))
            results.put("PDFTab.Data.CheckingSheet.Position.text",  (int)keyValuePair.get("PDFTab.Data.CheckingSheet.Position.text"));
        else
            results.put("PDFTab.Data.CheckingSheet.Position.text", -1);
        return results;
    }

    static public String getQuotationText(){
        if (keyValuePair.containsKey("PDFTab.Data.Quotation.Text"))
             return (String)keyValuePair.get("PDFTab.Data.Quotation.Text");
        return "";
    }

    static public String getCostingText(){
        if (keyValuePair.containsKey("PDFTab.Data.CostingSheet.Text"))
             return (String)keyValuePair.get("PDFTab.Data.CostingSheet.Text");
        return "";
    }

    static public String getLogoPath(){
        if(keyValuePair.containsKey("Logo.Path"))
            return (String) keyValuePair.get("Logo.Path");
        return "C:\\Logo.PNG";
    }

    static public String getCheckingSheetText(){
        if (keyValuePair.containsKey("PDFTab.Data.CheckingSheet.Text"))
            return (String)keyValuePair.get("PDFTab.Data.CheckingSheet.Text");
        return "";
    }

    static public boolean hasQuotationText(){
        if (keyValuePair.containsKey("PDFTab.Enable.Quotation.Text"))
            return !(boolean)keyValuePair.get("PDFTab.Enable.Quotation.Text");
        return false;
    }

    static public boolean hasCostingText(){
        if (keyValuePair.containsKey("PDFTab.Enable.CostingSheet.Text"))
            return !(boolean)keyValuePair.get("PDFTab.Enable.CostingSheet.Text");
        return false;
    }

    static  public boolean hasCheckingText(){
        if (keyValuePair.containsKey("PDFTab.Enable.CheckingSheet.Text"))
            return !(boolean)keyValuePair.get("PDFTab.Enable.CheckingSheet.Text");
        return false;
    }

    static public boolean isPDFTadEnable(){
        if (keyValuePair.containsKey("PDFTab.Enable"))
            return (boolean)keyValuePair.get("PDFTab.Enable");
        return true;
    }

    static public String getImportPath(){
        if(keyValuePair.containsKey("PathsTab.Data.Import"))
            return (String) keyValuePair.get("PathsTab.Data.Import");
        return "";
    }

    static public String getExportPath(){
        if(keyValuePair.containsKey("PathsTab.Data.Export"))
            return (String) keyValuePair.get("PathsTab.Data.Export");
        return "";
    }

    static public boolean getImportOnStartUp(){
        if(keyValuePair.containsKey("PathsTab.Data.ImportOnStartUp"))
            return (boolean) keyValuePair.get("PathsTab.Data.ImportOnStartUp");
        return false;
    }

    static public List<User> getAllUsers(){
        if(keyValuePair.containsKey("UsersTab.Data.Users"))
            return (ArrayList<User>) keyValuePair.get("UsersTab.Data.Users");
        return new ArrayList<>();
    }

    static public User getMainUser(){
        List<User> users =getAllUsers();
        if(users==null)
            return new User("Default","User","0000000000","DefaultUser@gmail.com",false);
        return users.stream()
                    .filter(user->user.isMainUser())
                    .findAny()
                    .orElse(null);
    }

    static public boolean isPathsTabEnable(){
        if(keyValuePair.containsKey("PathsTab.Enable"))
            return (boolean) keyValuePair.get("PathsTab.Enable");
        return true;
    }

    static public boolean isPathsExportEnable(){
        if(keyValuePair.containsKey("PathsTab.Enable.Export"))
            return (boolean) keyValuePair.get("PathsTab.Enable.Export");
        return true;
    }

    static public boolean isPathsImportEnable(){
        if(keyValuePair.containsKey("PathsTab.Enable.Import"))
            return (boolean) keyValuePair.get("PathsTab.Enable.Import");
        return true;
    }

    static public boolean isUsersTabEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable"))
            return (boolean) keyValuePair.get("UsersTab.Enable");
        return true;
    }

    static public boolean isUsersTabAddButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.AddButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.AddButton");
        return true;
    }

    static public boolean isUsersTabChangesButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.ChangesButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.ChangesButton");
        return true;
    }

    static public boolean isUsersTabMakeMainOnlyEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.MakeMainOnly"))
            return (boolean) keyValuePair.get("UsersTab.Enable.MakeMainOnly");
        return false;
    }

    static public boolean isUsersTabRemoveButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.RemoveButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.RemoveButton");
        return true;
    }

    static public void applyChanges(Map<String, Object> newValues) throws IOException {
        List<String> keys = Lists.newArrayList(newValues.keySet());
        boolean hasChanged=false;
        for (String key:  keys) {
            if(keyValuePair.containsKey(key))
                keyValuePair.replace(key,newValues.get(key));
            else
                keyValuePair.put(key,newValues.get(key));
            hasChanged=true;
        }
        if (hasChanged){
            FileWriter file = new FileWriter(absolutePath);
            BufferedWriter output = new BufferedWriter(file);
            String data = formatData();
            output.write(data);
            output.close();
        }

    }

    static private String formatData(){
        String dataText =keyValuePair.toString();
        dataText=dataText.substring(1,dataText.length()-1);
        String outputText ="";
        char character;
        boolean inArray=false;
        boolean inClass=false;
        for(int counter=0; counter<dataText.length();counter++) {
            character =dataText.charAt(counter);
            if(character=='['){
                inArray=true;
            }
            else if(character==']'){
                inArray=false;
            }else if(character=='{'){
                inClass=true;
            }else if(character=='}'){
                inClass=false;

            }

            if(character == ','&&!inArray&&!inClass){
                outputText+="\n";
                if(counter+1<=dataText.length())
                    counter++;
            }else{
                outputText+=character;
            }
            if(counter+1<dataText.length()&&dataText.charAt(counter+1)==','&&!inClass){
                outputText+=" ";
            }else if(counter+1<dataText.length()&&dataText.charAt(counter)==','&&!inClass) {
                outputText += " ";
            }
        }
        return outputText;
    }

    static public Map<String,Object> getKeyValuePair() {
        return keyValuePair;
    }

    static public String getTheme() {
        if(keyValuePair.containsKey("ThemeTab.Theme")) {
            return (String)keyValuePair.get("ThemeTab.Theme");
        }
        return "darkTheme.css";
    }

    public static String getCSSPath() {
        if(keyValuePair.containsKey("CSS.Path")) {
            return (String)keyValuePair.get("CSS.Path");
        }
        return "C:\\Users\\User\\OneDrive\\Work\\SP\\Invoice\\src\\quickQuotes\\CSS";
    }

    public static String getFXMLSettingsPath() {
        if(keyValuePair.containsKey("FXML.Settings.Path")) {
            return (String)keyValuePair.get("FXML.Settings.Path");
        }
        return "C:\\Users\\User\\OneDrive\\Work\\SP\\Invoice\\src\\quickQuotes\\fxml\\Settings.fxml";
    }

    public static String getGroupPath() {
        if(keyValuePair.containsKey("Groups.Path")) {
            return (String)keyValuePair.get("Groups.Path");
        }
        return "C:\\Users\\User\\OneDrive\\Work\\SP\\Invoice\\src\\quickQuotes\\resource\\groups.txt";
    }
}
