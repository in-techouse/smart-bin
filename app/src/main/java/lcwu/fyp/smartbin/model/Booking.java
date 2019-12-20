package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Booking implements Serializable
{
    private String id, userId, startTime;
    private Location pickup;
    private double trashWeightig;
    private int payment;

    public Booking() {
    }

    public Booking(String id, String userId, String startTime, Location pickup, double trashWeightig, int payment) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.pickup = pickup;
        this.trashWeightig = trashWeightig;
        this.payment = payment;
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

    public Location getPickup() {
        return pickup;
    }

    public void setPickup(Location pickup) {
        this.pickup = pickup;
    }

    public double getTrashWeightig() {
        return trashWeightig;
    }

    public void setTrashWeightig(double trashWeightig) {
        this.trashWeightig = trashWeightig;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }
}
