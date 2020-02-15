package lcwu.fyp.smartbin.model;

import android.location.Location;

import java.io.Serializable;

public class Booking implements Serializable
{
    private String id, userId, startTime;
    private String pickup , status , driverId;
    private double trashWeightig , lat , lng;
    private int payment;

    public Booking() {
    }

    public Booking(String id, String userId, String startTime, String pickup, double trashWeightig, int payment , String status , String driverId , double lat , double lng) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.pickup = pickup;
        this.trashWeightig = trashWeightig;
        this.payment = payment;
        this.status = status;
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
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


}
