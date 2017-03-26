package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class Attraction {
    String name;
    String description;
    @SerializedName("portrait_image") String photoUrl;

    public Attraction(String name, String description, String photoUrl) {
        this.name        = name;
        this.description = description;
        this.photoUrl    = photoUrl;
    }

    @Override
    public String toString() {
        return "Attraction(" + name + ", " + description + ", " + photoUrl + ")";
    }
}
