package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class PointOfInterest {
    public int id;
    public int order;
    public String name;
    public String description;
    public @SerializedName("portrait_image") String photoUri;

    public PointOfInterest(int id, int order, String name, String description, String photoUri) {
        this.id             = id;
        this.order          = order;
        this.name           = name;
        this.description    = description;
        this.photoUri       = photoUri;
    }

    @Override
    public String toString() {
        return "PointOfInterest {\n  name: " + name + "\n  description: " + description +
                "\n  photoUri: " + photoUri + "\n}";
    }
}
