package lcwu.fyp.smartbin.model;

import java.util.jar.Attributes;

public class User
{

    private String firstName, lastName, e_mail, id;

    public User() {
    }

    public User(String firstName, String lastName, String e_mail, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.e_mail = e_mail;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
