package sample.tools;

import com.google.common.collect.Lists;
import sample.controllers.SettingsController;
import sample.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeListener {

    private Map<String,Object> changes;
    private Map<String,Object> oldValue;
    private SettingsController settingsController;

    public ChangeListener(SettingsController settingsController, Map<String, Object> keyValuePair) {
        this.settingsController = settingsController;
        changes = new HashMap<>();
        oldValue = new HashMap<>(keyValuePair);
    }

    public void replace(String key, Object newValue){
        if(changes.containsKey(key)) {
            if(newValue!=null){
                changes.replace(key, newValue);
            }else{
                changes.remove(key);
            }
        }else{
            if(newValue!=null) {
                changes.put(key, newValue);
            }
        }
        checkForChange();
    }

    private void checkForChange() {
        boolean hasChange=false;
        List<String> keys = Lists.newArrayList(changes.keySet());
        for (String key:  keys) {
            boolean equalsCheck = !(changes.get(key).equals(oldValue.get(key)));
            if(changes.get(key) instanceof ArrayList && oldValue.get(key) instanceof ArrayList){
                List<User> listOfUser1 = (ArrayList<User>)changes.get(key);
                List<User> listOfUser2 = (ArrayList<User>)oldValue.get(key);
                if(listOfUser1.size()==listOfUser2.size()){
                    for (int counter = 0; counter < listOfUser1.size(); counter++) {
                        User user1 = listOfUser1.get(counter);
                        User user2 = listOfUser2.get(counter);
                        if(user2!=null&&!user1.equals(user2)){
                            equalsCheck=true;
                        }
                    }
                }else{
                    equalsCheck=true;
                }
            }
            if(equalsCheck ){
                hasChange=true;
            }else{
                changes.remove(key);
            }
        }
        settingsController.enableDisableApplyButton(!hasChange);
    }

    public Map<String,Object> getNewValues(){
        return changes;
    }

    public void update(Map<String, Object> keyValuePair) {
        oldValue=keyValuePair;
        changes=new HashMap<>();
    }
}
