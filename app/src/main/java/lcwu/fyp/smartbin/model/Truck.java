package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Truck implements Serializable {
    private String id, driverId, truckModel, registrationNumber, truckCapacity;

    public Truck() {
    }

    public Truck(String id, String registrationNumber, String truckModel, String truckCapacity, String driverId) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.truckModel = truckModel;
        this.truckCapacity = truckCapacity;
        this.driverId = driverId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getTruckModel() {
        return truckModel;
    }

    public void setTruckModel(String truckModel) {
        this.truckModel = truckModel;
    }

    public String getTruckCapacity() {
        return truckCapacity;
    }

    public void setTruckCapacity(String truckCapacity) {
        this.truckCapacity = truckCapacity;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
