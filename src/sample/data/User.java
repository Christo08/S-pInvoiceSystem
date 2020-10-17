package sample.data;

import java.util.Objects;

public class User {
    private String name="";
    private String surname="";
    private String number="";
    private String email="";
    private boolean mainUser=false;
    private String id="";

    public User(User other){
        this.name = other.name;
        this.surname = other.surname;
        this.number = other.number;
        this.email = other.email;
        this.mainUser = other.mainUser;
        this.id =other.id;
    }

    public User(String inputData){
        inputData = inputData.substring(1,inputData.length()-1);
        String[] data = inputData.split(",");
        name=data[0];
        surname=data[1];
        number=data[2];
        email=data[3];
        if(data[4].equals("false"))
            mainUser=false;
        else
            mainUser=true;
        id=data[5];
    }

    public User( String name, String surname, String number, String email, boolean mainUser) {
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.email = email;
        this.mainUser = mainUser;
    }

    public User() {
        this("","","","",false);
    }

    @Override
    public String toString() {
        return "{" +
                name +
                "," + surname +
                "," + number +
                "," + email +
                "," + mainUser +
                "," + id +
                '}';
    }

    public String pdfString(){
        return  name+" "+surname+"\n"+
                number+"\n"+
                email+"\n"+
                "www.sppowerunits.co.za\n"+
                "1121 Steve Biko Road,\n"+
                "Wonderboom South, 0084";
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public void changeMainUserStatus(boolean newStatus){
        mainUser=newStatus;
    }

    public boolean isMainUser() {
        return mainUser;
    }

    public void setId(int userCounter){
        String initials =name.charAt(0)+""+surname.charAt(0);
        id = initials+userCounter;
    }

    public String getId(){
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMainUser(boolean mainUser) {
        this.mainUser = mainUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.getId());
    }

    public boolean equals(User users1){
        if (this == users1) return true;
        if (users1 == null || getClass() != users1.getClass()) return false;
        return id.equals(users1.getId())&&name.equals(users1.getName())&&surname.equals(users1.getSurname())&&
                number.equals(users1.getNumber())&&email.equals(users1.getEmail())&&mainUser==users1.isMainUser();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, number, email, mainUser);
    }
}
