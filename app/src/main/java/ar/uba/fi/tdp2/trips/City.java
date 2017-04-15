package ar.uba.fi.tdp2.trips;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("id") private int cityId;
    @SerializedName("country_name") private String country;
    private String name;
    private Double latitude;
    private Double longitude;

    public City(String name, String country, Double latitude, Double longitude, int cityId) {
        this.name       = name;
        this.cityId     = cityId;
        this.country    = country;
        this.latitude   = latitude;
        this.longitude  = longitude;
    }

    @Override
    public String toString() {
        return "City(" + cityId + ", " + name + ", " + country + ", " + latitude + ", " + longitude + ")";
    }

    public String getName() {
        return name;
    }

    public int getCityId() {
        return cityId;
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
