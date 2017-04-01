package ar.uba.fi.tdp2.trips;

public class City {
    String name;
    String country;
    Double latitude;
    Double longitude;

    public City(String name, String country, Double latitude, Double longitude) {
        this.name       = name;
        this.country    = country;
        this.latitude   = latitude;
        this.longitude  = longitude;
    }

    @Override
    public String toString() {
        return "City(" + name + ", " + country + ", " + latitude + ", " + longitude + ")";
    }
}
