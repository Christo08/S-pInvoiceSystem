package sample.data;

public class User {
    private String name;
    private String surname;
    private String number;
    private String email;
    private boolean mainUser;
    private String id;

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

    public User( String name, String surname, String number, String email) {
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                name +
                "," + surname +
                "," + number +
                "," + email +
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
}
