package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private String id, userId, startTime;
    private String pickup, status, driverId;
    private double trashWeight, lat, lng;
    private int payment;
    private int amountCharged;

    public Booking() {
    }

    public Booking(String id, String userId, String startTime, String pickup, String status, String driverId, double trashWeight, double lat, double lng, int payment, int amountCharged) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.pickup = pickup;
        this.status = status;
        this.driverId = driverId;
        this.trashWeight = trashWeight;
        this.lat = lat;
        this.lng = lng;
        this.payment = payment;
        this.amountCharged = amountCharged;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public double getTrashWeight() {
        return trashWeight;
    }

    public void setTrashWeight(double trashWeight) {
        this.trashWeight = trashWeight;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public int getAmountCharged() {
        return amountCharged;
    }

    public void setAmountCharged(int amountCharged) {
        this.amountCharged = amountCharged;
    }
}
