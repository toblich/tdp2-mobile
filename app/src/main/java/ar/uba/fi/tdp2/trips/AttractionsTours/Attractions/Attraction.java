package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ar.uba.fi.tdp2.trips.AttractionsTours.Tours.Tour;
import ar.uba.fi.tdp2.trips.Cities.City;
import ar.uba.fi.tdp2.trips.Reviews.Review;

public class Attraction {
    public int id;
    public String name;
    public String description;
    public @SerializedName("portrait_image") String photoUri;
    public @SerializedName("audio_guide") String audioguide;

    public String address;
    public String url;
    public String phone;
    public Double price;
    public @SerializedName("average_rating") float avgRating;

    public @SerializedName("average_visit_duration") int duration;
    public List<Review> reviews;
    public @SerializedName("own_review") Review ownReview;
    public @SerializedName("opening_hours") List<OpeningHour> openingHours;
    public List<Tour> tours;
    public City city;

    public double latitude;
    public double longitude;

    public boolean favorite;
    public boolean visited;

    public Attraction(int id, String name, String description, String photoUri, String audioguide,
                      String address, String url, String phone, double price, int duration,
                      List<Review> reviews, Review ownReview, List<Tour> tours, float avgRating,
                      boolean favorite, boolean visited) {

        this.id          = id;
        this.name        = name;
        this.description = description;
        this.photoUri    = photoUri;
        this.audioguide  = audioguide;

        this.address = address;
        this.url = url;
        this.phone = phone;
        this.price = price;
        this.duration = duration;
        this.avgRating = avgRating;

        this.reviews = reviews;
        this.ownReview = ownReview;
        this.tours = tours;

        this.favorite = favorite;
        this.visited = visited;
    }

    @Override
    public String toString() {
        String myRev = ownReview != null ? "\n  ownReview: " + ownReview.toString() : "";
        String revs = reviews != null ? "\n  reviews: " + reviews.toString() : "";
        String opens = openingHours != null ? "\n  " + openingHours.toString() : "";

        return "Attraction {\n  id: " + String.valueOf(id) + "\n  name: " + name +
                "\n  description: " + description + "\n  photoUri: " + photoUri +  revs + myRev +
                opens + "\n}";
    }

    public String getFullAddress() {
        return address + ", " + city.getName() + ", " + city.getCountry();
    }

    public class OpeningHour {
        public String day;
        public String start;
        public String end;

        public OpeningHour(String day, String start, String end) {
            this.day = day;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            if (start == null || end == null) {
                return "OpeningHour: " + day + " (24hs)";
            }
            return "OpeningHour {\n  day: " + day + "\n  start: " + start + "\n  end: " + end + "\n}";
        }
    }
}
