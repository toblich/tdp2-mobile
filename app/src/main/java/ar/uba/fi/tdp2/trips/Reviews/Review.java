package ar.uba.fi.tdp2.trips.Reviews;

import com.google.gson.annotations.SerializedName;

public class Review {
    public @SerializedName("calification") int rating;
    public String user;
    public String date;
    public String text;

    public Review(int rating, String user, String date, String text) {
        this.rating = rating;
        this.user = user;
        this.date = date;
        this.text = text;
    }

    public Review(int rating, String text) {
        this.rating = rating;
        this.text = text;
        this.date = null;
        this.user = null;
    }

    @Override
    public String toString() {
        return "Review {\n  rating: " + String.valueOf(rating) + "\n  user: " + user +
                "\n  date: " + date + "\n  text: " + text + "\n}";
    }

    public Review clone() {
        return new Review(rating, user, date, text);
    }
}
