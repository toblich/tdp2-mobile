package ar.uba.fi.tdp2.trips;

public class Attraction {
    String name;
    String description;
    String portrait_image; // TODO see if it's possible to make this camelCase (using snake_case for response mapping)

    public Attraction(String name, String description, String portrait_image) {
        this.name        = name;
        this.description = description;
        this.portrait_image = portrait_image;
    }

    @Override
    public String toString() {
        return "Attraction(" + name + ", " + description + ", " + portrait_image + ")";
    }
}
