package ar.uba.fi.tdp2.trips.Notifications;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id") int notificationId;
    String message;
    String url;
    String title;
    @SerializedName("created_at") long dateInMilliseconds;

    public Notification(int notificationId, String message, String url, String title, long dateInMilliseconds) {
        this.notificationId = notificationId;
        this.message = message;
        this.url = url;
        this.title = title;
        this.dateInMilliseconds = dateInMilliseconds;
    }

    @Override
    public String toString() {
        return "Notification(" + notificationId + ")";
    }
}

