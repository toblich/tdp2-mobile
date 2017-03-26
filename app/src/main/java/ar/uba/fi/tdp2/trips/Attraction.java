package ar.uba.fi.tdp2.trips;

import android.net.Uri;

public class Attraction {
    String name;
    String description;
    String photoUri;

    public Attraction(String name, String description, String photoUri) {
        this.name        = name;
        this.description = description;
        this.photoUri    = photoUri;
    }
}
