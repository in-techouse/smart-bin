package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Notification implements Serializable
{
    private String id,bookingId,userId,notifications , drivierId ,date ,driverText , userText;
    private boolean read;


    public Notification() {
    }

    public Notification(String id, String bookingId, String userId, String notifications , String driverId , boolean read , String date , String driverText , String userText) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.notifications = notifications;
        this.drivierId = driverId;
        this.read = read;
        this.date = date;
        this.driverText = driverText;
        this.userText = userText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    public String getDriverId(){return drivierId;}

    public void setDriverId(String driverId){this.drivierId = driverId;}

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDriverText() {
        return driverText;
    }

    public void setDriverText(String driverText) {
        this.driverText = driverText;
    }

    public String getUserText() {
        return driverText;
    }

    public void setUserText(String driverText) {
        this.driverText = driverText;
    }





}
