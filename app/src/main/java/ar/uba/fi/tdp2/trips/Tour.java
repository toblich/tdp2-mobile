package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tour {

    private int id;
    private String name;
    private String description;
    private @SerializedName("portrait_image") String photoUri;
    private @SerializedName("estimated_time") int duration;
    private List<Attraction> attractions;

    public Tour(int id, String name, String description, int duration, String photoUri, List<Attraction> attractions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photoUri = photoUri;
        this.duration = duration;
        this.attractions = (attractions != null) ? attractions : new ArrayList<Attraction>();
    }


    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "Tour {\n  id: %d\n  name: %s\n  description: %s\n  duration: %d\n  photoUri: %s\n  attractions: " + getAttractions().toString() +"\n}",
                id, name, description, duration, photoUri
        );
    }

    public List<Attraction> getAttractions() {
        if (attractions == null) {
            attractions = new ArrayList<>();
        }
        return attractions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public int getDuration() {
        return duration;
    }
}
