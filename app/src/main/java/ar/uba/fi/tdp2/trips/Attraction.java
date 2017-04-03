package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class Attraction {
    public int id;
    public String name;
    public String description;
    public @SerializedName("portrait_image") String photoUri;

    public String address;
    public String url;
    public String phone;
    public double price;
    // TODO add opening hours and reviews
    public @SerializedName("average_visit_duration") int duration;

    public Attraction(int id, String name, String description, String photoUri, String address,
                      String url, String phone, double price, int duration) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.photoUri    = photoUri;

        this.address = address;
        this.url = url;
        this.phone = phone;
        this.price = price;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Attraction {\n  name: " + name + "\n  description: " + description +
                "\n  photoUri: " + photoUri + "\n}";
    }
}
