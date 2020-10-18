package sample.controllers;

import com.google.common.collect.Lists;
import sample.data.User;
import java.io.*;
import java.security.Key;
import java.util.*;

public class SettingsFileController {
    String fileName = "settings.txt";
    public static String recourcePath = new File("src/sample/resource/").getAbsolutePath();
    String absolutePath = recourcePath+"/"+fileName;
    Map<String,Object> keyValuePair;
    private boolean usersDataHasChanged=false;
    private boolean pdfDataHasChanged=false;
    private boolean pathsDataHasChanged=false;

    public SettingsFileController() {
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
                           String[] stringUsers = lineSplit[1].substring(1,lineSplit[1].length()-1).split(", ");
                           List<User> users = new ArrayList<>();
                           for (int counter=0; counter< stringUsers.length;counter++ ){
                               users.add(new User(stringUsers[counter].trim()));
                           }
                           keyValuePair.put(lineSplit[0],users);
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
            System.out.println(e);
        }

    }

    public Map<String, Integer> getQuotationPositions(){
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

    public Map<String, Integer> getCostingPositions(){
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

    public String getQuotationText(){
        if (keyValuePair.containsKey("PDFTab.Data.Quotation.Text"))
             return (String)keyValuePair.get("PDFTab.Data.Quotation.Text");
        return "";
    }

    public String getCostingText(){
        if (keyValuePair.containsKey("PDFTab.Data.CostingSheet.Text"))
             return (String)keyValuePair.get("PDFTab.Data.CostingSheet.Text");
        return "";
    }

    public boolean hasQuotationText(){
        if (keyValuePair.containsKey("PDFTab.Enable.Quotation.Text"))
            return !(boolean)keyValuePair.get("PDFTab.Enable.Quotation.Text");
        return false;
    }

    public boolean hasCostingText(){
        if (keyValuePair.containsKey("PDFTab.Enable.CostingSheet.Text"))
            return !(boolean)keyValuePair.get("PDFTab.Enable.CostingSheet.Text");
        return false;
    }

    public boolean isPDFTadEnable(){
        if (keyValuePair.containsKey("PDFTab.Enable"))
            return (boolean)keyValuePair.get("PDFTab.Enable");
        return false;
    }

    public String getImportPath(){
        if(keyValuePair.containsKey("PathsTab.Data.Import"))
            return (String) keyValuePair.get("PathsTab.Data.Import");
        return "";
    }

    public String getExportPath(){
        if(keyValuePair.containsKey("PathsTab.Data.Export"))
            return (String) keyValuePair.get("PathsTab.Data.Export");
        return "";
    }

    public boolean getImportOnStartUp(){
        if(keyValuePair.containsKey("PathsTab.Data.ImportOnStartUp"))
            return (boolean) keyValuePair.get("PathsTab.Data.ImportOnStartUp");
        return false;
    }

    public List<User> getAllUsers(){
        if(keyValuePair.containsKey("UsersTab.Data.Users"))
            return (ArrayList<User>) keyValuePair.get("UsersTab.Data.Users");
        return new ArrayList<>();
    }

    public User getMainUser(){
        List<User> users =getAllUsers();
        if(users==null)
            return new User("Default","User","0000000000","DefaultUser@gmail.com",false);
        return users.stream()
                    .filter(user->user.isMainUser())
                    .findAny()
                    .orElse(null);
    }

    public boolean isChanged(){
        return (usersDataHasChanged||pdfDataHasChanged||pathsDataHasChanged);
    }

    public boolean isPathsTabEnable(){
        if(keyValuePair.containsKey("PathsTab.Enable"))
            return (boolean) keyValuePair.get("PathsTab.Enable");
        return true;
    }

    public boolean isPathsExportEnable(){
        if(keyValuePair.containsKey("PathsTab.Enable.Export"))
            return (boolean) keyValuePair.get("PathsTab.Enable.Export");
        return true;
    }

    public boolean isPathsImportEnable(){
        if(keyValuePair.containsKey("PathsTab.Enable.Import"))
            return (boolean) keyValuePair.get("PathsTab.Enable.Import");
        return true;
    }

    public boolean isUsersTabEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable"))
            return (boolean) keyValuePair.get("UsersTab.Enable");
        return true;
    }

    public boolean isUsersTabAddButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.AddButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.AddButton");
        return true;
    }

    public boolean isUsersTabChangesButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.ChangesButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.ChangesButton");
        return true;
    }

    public boolean isUsersTabMakeMainOnlyEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.MakeMainOnly"))
            return (boolean) keyValuePair.get("UsersTab.Enable.MakeMainOnly");
        return false;
    }

    public boolean isUsersTabRemoveButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.RemoveButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.RemoveButton");
        return true;
    }

    public void applyChanges(Map<String, Object> newValues) throws IOException {
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

    private String formatData(){
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

    public Map<String,Object> getKeyValuePair() {
        return keyValuePair;
    }

    public String getTheme() {
        if(keyValuePair.containsKey("ThemeTab.Theme"))
            return (String)keyValuePair.get("ThemeTab.Theme");
        return "darkTheme.css";
    }
}
