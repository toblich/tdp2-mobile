package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class Attraction {
    String name;
    String description;
    @SerializedName("portrait_image") String photoUri;

    public Attraction(String name, String description, String photoUri) {
        this.name        = name;
        this.description = description;
        this.photoUri    = photoUri;
    }

    @Override
    public String toString() {
        return "Attraction(" + name + ", " + description + ", " + photoUri + ")";
    }
}
