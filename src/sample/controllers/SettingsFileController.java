package sample.controllers;

import sample.data.User;
import java.io.*;
import java.util.*;

public class SettingsFileController {
    String fileName = "settings.txt";
    String absolutePath = "D:\\Users\\Jeandre Botha\\Documents\\S-pInvoiceSystem\\src\\sample\\resource\\"+fileName;
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
                    String[] lineSplit = line.split(" = ");
                    if(lineSplit[1].equals("true")){
                        keyValuePair.put(lineSplit[0],true);
                    }
                    else if(lineSplit[1].equals("false")){
                        keyValuePair.put(lineSplit[0],false);
                    }
                    else if(lineSplit[1].charAt(0) =='['&&lineSplit[1].charAt(lineSplit[1].length()-1) ==']'){
                        List<String> elements = new ArrayList<>(Arrays.asList(lineSplit[1].substring(1,lineSplit[1].length()-1).split(" , ")));
                        if(lineSplit[0].equals("UsersTab.Data.Users")){
                            List<User> users = new ArrayList<>();
                            for (String user: elements) {
                                users.add(new User(user));
                            }
                            keyValuePair.put(lineSplit[0],users);
                        }
                        if(lineSplit[0].equals("PDFTab.Data.Positions")){
                            List<Integer> positions = new ArrayList<>();
                            for (String position: elements) {
                                positions.add(Integer.parseInt(position));
                            }
                            keyValuePair.put(lineSplit[0],positions);
                        }
                    }
                    else if(lineSplit[0].equals("UsersTab.Data.UsersCounter")){
                        keyValuePair.put(lineSplit[0],Integer.parseInt(lineSplit[1]));
                    }
                    else{
                        keyValuePair.put(lineSplit[0],lineSplit[1]);
                    }
                }
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public List<Integer> getPDFPositions(){
        if(keyValuePair.containsKey("PDFTab.Data.Positions"))
            return (ArrayList<Integer>)keyValuePair.get("PDFTab.Data.Positions");
        List<Integer> defaultValues = new ArrayList<>();
        defaultValues.add(1);
        defaultValues.add(2);
        defaultValues.add(4);
        defaultValues.add(3);
        return defaultValues;
    }

    public Map<String,Integer> getPDFPositionMap(){
        List<Integer> positions = getPDFPositions();
        Map<String,Integer> positionMap = new HashMap<>();
        positionMap.put("info", positions.get(0));
        positionMap.put("costingSheet", positions.get(1));
        positionMap.put("quotationSheet", positions.get(2));
        positionMap.put("additionalText", positions.get(3));
        return positionMap;
    }

    public void setPDFPositions(List<Integer> newPositions){
        keyValuePair.replace("PDFTab.Data.Positions",newPositions);
        pdfDataHasChanged=true;
    }

    public String getPDFText(){
        if(keyValuePair.containsKey("PDFTab.Data.Text"))
            return (String) keyValuePair.get("PDFTab.Data.Text");
        return "";
    }

    public void setPDFText(String newPDFText){
        pdfDataHasChanged=true;
        keyValuePair.replace("PDFTab.Data.Text",newPDFText);
    }


    public String getImportPath(){
        if(keyValuePair.containsKey("PathsTab.Data.Import"))
            return (String) keyValuePair.get("PathsTab.Data.Import");
        return "";
    }

    public void setImportPath(String newPath){
        keyValuePair.replace("PathsTab.Data.Import",newPath);
        pathsDataHasChanged=true;
    }

    public String getExportPath(){
        if(keyValuePair.containsKey("PathsTab.Data.Export"))
            return (String) keyValuePair.get("PathsTab.Data.Export");
        return "";
    }

    public void setExportPath(String newPath){
        keyValuePair.replace("PathsTab.Data.Export",newPath);
        pathsDataHasChanged=true;
    }

    public boolean getImportOnStartUp(){
        if(keyValuePair.containsKey("PathsTab.Data.ImportOnStartUp"))
            return (boolean) keyValuePair.get("PathsTab.Data.ImportOnStartUp");
        return false;
    }

    public void setImportOnStartUp(boolean newValue){
        keyValuePair.replace("PathsTab.Data.ImportOnStartUp",newValue);
        pathsDataHasChanged=true;
    }

    public List<User> getAllUsers(){
        if(keyValuePair.containsKey("UsersTab.Data.Users"))
            return (ArrayList<User>) keyValuePair.get("UsersTab.Data.Users");
        return new ArrayList<>();
    }

    public User getMainUser(){
        List<User> users =getAllUsers();
        if(users==null)
            return new User("Default","User","0000000000","DefaultUser@gmail.com");
        return users.stream()
                    .filter(user->user.isMainUser())
                    .findAny()
                    .orElse(null);
    }

    public void setMainUser(String userId){
        usersDataHasChanged=true;
    }

    public void removeUser(User user){
        usersDataHasChanged=true;
        ((List<User>)keyValuePair.get("UsersTab.Data.Users")).remove(user);
    }

    public void addUser(User user){
        usersDataHasChanged=true;
        int usersCounter=(int)keyValuePair.get("UsersTab.Data.UsersCounter");
        usersCounter++;
        keyValuePair.replace("UsersTab.Data.UsersCounter",usersCounter);
        user.setId(usersCounter);
        ((List<User>)keyValuePair.get("UsersTab.Data.Users")).add(user);
    }

    public void changesUser(User user){
        usersDataHasChanged=true;
        ((List<User>)keyValuePair.get("UsersTab.Data.Users")).remove(user);
        ((List<User>)keyValuePair.get("UsersTab.Data.Users")).add(user);
    }

    public boolean isUsersDataHasChanged() {
        return usersDataHasChanged;
    }

    public boolean isChanged(){
        return (usersDataHasChanged||pdfDataHasChanged||pathsDataHasChanged);
    }

    public boolean isPDFTabEnable(){
        if(keyValuePair.containsKey("PDFTab.Enable"))
            return (boolean) keyValuePair.get("PDFTab.Enable");
        return true;
    }

    public boolean isPDFTextEnable(){
        if(keyValuePair.containsKey("PDFTab.Enable.Text"))
            return (boolean) keyValuePair.get("PDFTab.Enable.Text");
        return true;
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

    public boolean isPathsExportImport(){
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
        if(keyValuePair.containsKey("UsersTab.Enable.AddButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.AddButton");
        return false;
    }

    public boolean isUsersTabRemoveButtonEnable(){
        if(keyValuePair.containsKey("UsersTab.Enable.RemoveButton"))
            return (boolean) keyValuePair.get("UsersTab.Enable.RemoveButton");
        return true;
    }

    public void applyChanges(){
        if(isChanged()){
            String data = formatData();
            try(BufferedWriter  bufferedReader = new BufferedWriter (new FileWriter(absolutePath))) {
                bufferedReader.write(data);
            }catch (Exception e){
                System.out.println(e);
            }
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
        outputText.replace("="," = ");
        return outputText;
    }
}
