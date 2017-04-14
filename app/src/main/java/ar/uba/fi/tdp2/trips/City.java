package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class City {
    private String name;
    @SerializedName("country_name") private String country;
    private Double latitude;
    private Double longitude;

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

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
