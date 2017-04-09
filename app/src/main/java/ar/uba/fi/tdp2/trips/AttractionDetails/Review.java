package ar.uba.fi.tdp2.trips.AttractionDetails;

import com.google.gson.annotations.SerializedName;

public class Review {
    public @SerializedName("calification") int qualification;
    public String user;
    public String date;
    public String text;

    public Review(int qualification, String user, String date, String text) {
        this.qualification = qualification;
        this.user = user;
        this.date = date;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Review {\n  qualification: " + String.valueOf(qualification) + "\n  user: " + user +
                "\n  date: " + date + "\n  text: " + text + "\n}";
    }
}
