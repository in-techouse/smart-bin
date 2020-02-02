package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Driver implements Serializable {
    private String email;
    private String id;
    private String lisenceNumber;
    private String name;
    private String phoneNumber;

    public Driver(String email, String id, String lisenceNumber, String name, String phoneNumber) {
        this.email = email;
        this.id = id;
        this.lisenceNumber = lisenceNumber;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Driver() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLisenceNumber() {
        return lisenceNumber;
    }

    public void setLisenceNumber(String lisenceNumber) {
        this.lisenceNumber = lisenceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
