package ar.uba.fi.tdp2.trips;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ar.uba.fi.tdp2.trips.AttractionDetails.Review;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {
    //Get the list of all the cities
    @GET("/cities")
    Call<List<City>> getCities();

    //Get the list of the attractions for the city and radius
    @GET("/attractions")
    Call<List<Attraction>> getAttractionsRadiusAndCityID(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("radius")    double radius,
            @Query("city_id")   int city_id
    );

    //Get the list of the nearest attractions
    @GET("/attractions")
    Call<List<Attraction>> getAttractions(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("radius")    double radius
    );

    //Get the details of a specific attraction
    @GET("/attractions/{attractionId}")
    Call<Attraction> getAttraction(
          @Path("attractionId") int attractionId
    );

    //Get the points of interest's list of a specific attraction
    @GET("/attractions/{attractionId}/point_of_interests")
    Call<List<PointOfInterest>> getPointsOfInterest(
          @Path("attractionId") int attractionId
    );

    //Get the details of a specific point of interest
    @GET("/attractions/{attractionId}/point_of_interests/{poiId}")
    Call<PointOfInterest> getPointOfInterest(
            @Path("attractionId") int attractionId,
            @Path("poiId") int poiId
    );

    // Get list of reviews for an attraction
    @GET("/attractions/{attractionId}/reviews")
    Call<List<Review>> getReviews(
      @Path("attractionId") int attractionId
    );

    // Post review for an attraction
    @POST("/attractions/{attractionId}/reviews")
    Call<Review> postReview(
        @Path("attractionId") int attractionId,
        @Header("Authorization") String bearer,
        @Body Review review
    );

    //Create a new user
    @POST("/users")
    Call<User> createUser(@Body User user);

    HttpLoggingInterceptor loggingInterceptor = (new HttpLoggingInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient = (new OkHttpClient.Builder())
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request request = original.newBuilder()
                            .header("Accept-Language", Locale.getDefault().getLanguage())
                            .header("Content-Language", Locale.getDefault().getLanguage())
                            .build();

                    return chain.proceed(request);
                }
            })
            .addInterceptor(loggingInterceptor)
            .build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            //TODO: IP, ya acomode apiary para que funcione bien sin tener que hardcodear los parámetros.
            //.baseUrl("http://192.168.0.49")
//            .baseUrl("https://private-0e956b-trips5.apiary-mock.com")
            .baseUrl("http://192.168.0.117")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
}
