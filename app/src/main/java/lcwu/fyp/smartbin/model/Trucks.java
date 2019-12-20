package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Trucks implements Serializable
{
    private String id, registrationNum,driverName, driverPicture, phnNum;
    private double rating;

    public Trucks() {
    }

    public Trucks(String id, String registrationNum, String driverName, String driverPicture, String phnNum, double rating) {
        this.id = id;
        this.registrationNum = registrationNum;
        this.driverName = driverName;
        this.driverPicture = driverPicture;
        this.phnNum = phnNum;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistrationNum() {
        return registrationNum;
    }

    public void setRegistrationNum(String registrationNum) {
        this.registrationNum = registrationNum;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPicture() {
        return driverPicture;
    }

    public void setDriverPicture(String driverPicture) {
        this.driverPicture = driverPicture;
    }

    public String getPhnNum() {
        return phnNum;
    }

    public void setPhnNum(String phnNum) {
        this.phnNum = phnNum;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
