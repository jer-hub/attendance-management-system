

public class Student {
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int id;
    public  String firstname;

    public  String lastname;
    public  boolean status;

    public Student(int id, String firstname, String lastname, boolean status){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
    }

    public String getFirstname() {
        return firstname;
    }

    public  String getLastname() {
        return lastname;
    }

    public  boolean isStatus() {
        return status;
    }

}
