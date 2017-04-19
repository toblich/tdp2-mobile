package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class Tour {

    public int id;
    public String name;
    public String description;
    public @SerializedName("portrait_image") String photoUri;
    public @SerializedName("estimated_time") int estimatedTime ;


    public Tour(int id, String name, String description, String photoUri, int estimatedTime) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.photoUri    = photoUri;
        this.estimatedTime  = estimatedTime;
    }

    @Override
    public String toString() {

        return "Tour {\n  id: " + String.valueOf(id) + "\n  name: " + name +
                "\n  description: " + description + "\n  photoUri: " + photoUri +
                "\n estimated_time:" + estimatedTime + "\n}";
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

    public int getEstimatedTime() {
        return estimatedTime;
    }
}
