package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class PointOfInterest {
    public int id;
    public int order;
    public String name;
    public String description;
    public @SerializedName("portrait_image") String photoUri;
    public @SerializedName("audio_guide") String audioguide;

    public PointOfInterest(int id, int order, String name, String description, String photoUri, String audioguide) {
        this.id             = id;
        this.order          = order;
        this.name           = name;
        this.description    = description;
        this.photoUri       = photoUri;
        this.audioguide     = audioguide;
    }

    @Override
    public String toString() {
        return "PointOfInterest {\n  name: " + name + "\n  description: " + description +
                "\n  order: " + order + "\n}";
    }

    public String getOrder() {
        return String.valueOf(order);
    }
}
