package lcwu.fyp.smartbin.model;

import java.io.Serializable;

public class Notification implements Serializable
{
    private String id,bookingId,userId,notifications;

    public Notification() {
    }

    public Notification(String id, String bookingId, String userId, String notifications) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.notifications = notifications;
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
}
