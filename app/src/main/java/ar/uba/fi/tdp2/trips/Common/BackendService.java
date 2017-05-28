package ar.uba.fi.tdp2.trips.Common;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.Attraction;
import ar.uba.fi.tdp2.trips.Notifications.Notification;
import ar.uba.fi.tdp2.trips.PointsOfInterest.PointOfInterest;
import ar.uba.fi.tdp2.trips.Reviews.Review;
import ar.uba.fi.tdp2.trips.AttractionsTours.Tours.Tour;
import ar.uba.fi.tdp2.trips.Cities.City;
import ar.uba.fi.tdp2.trips.Multimedia.Gallery;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.facebook.FacebookSdk.getCacheDir;

public interface BackendService {
    //Get the list of all the cities
    @GET("/cities")
    Call<List<City>> getCities();

    //Get the list of tours for the city
    @GET("/cities/{cityId}/tours")
    Call<List<Tour>> getTours(
            @Path("cityId") int cityId
    );

    //Get the list of the nearest attractions
    @GET("/attractions")
    Call<List<Attraction>> getAttractions(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("radius")    double radius
    );

    //Get the list of the nearest attractions with auth
    @GET("/attractions")
    Call<List<Attraction>> getAttractionsWithAuth(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("radius")    double radius,
            @Header("Authorization") String bearer
    );

    //Get the details of a specific attraction
    @GET("/attractions/{attractionId}")
    Call<Attraction> getAttraction(
          @Path("attractionId") int attractionId
    );

    //Get the details of a specific attraction
    @GET("/attractions/{attractionId}")
    Call<Attraction> getAttractionWithAuth(
        @Path("attractionId") int attractionId,
        @Header("Authorization") String bearer
    );

    //Marked an attraction as favorite
    @POST("/attractions/{attractionId}/favorite")
    Call<Void> markFavoriteAttraction(
        @Path("attractionId") int attractionId,
        @Header("Authorization") String bearer
    );

    //Unmarked an attraction as favorite
    @DELETE("/attractions/{attractionId}/favorite")
    Call<Void> unmarkFavoriteAttraction(
            @Path("attractionId") int attractionId,
            @Header("Authorization") String bearer
    );

    //Marked an attraction as visited
    @POST("/attractions/{attractionId}/visited")
    Call<Void> markVisitedAttraction(
            @Path("attractionId") int attractionId,
            @Header("Authorization") String bearer
    );

    //Unmarked an attraction as visited
    @DELETE("/attractions/{attractionId}/visited")
    Call<Void> unmarkVisitedAttraction(
            @Path("attractionId") int attractionId,
            @Header("Authorization") String bearer
    );

    //Get the gallery of a specific attraction
    @GET("/attractions/{attractionId}/gallery")
    Call<Gallery> getAttractionGallery(
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

    //Get the gallery of a specific point of interest
    @GET("/attractions/{attractionId}/point_of_interests/{poiId}/gallery")
    Call<Gallery> getPointOfInterestGallery(
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
    Call<User> createUser(
            @Body User user
    );

    //LogIn
    @POST("/login")
    Call<Void> postAppOpened(
            @Query("device_token") String device_token,
            @Query("country") String country,
            @Header("Authorization") String bearer
    );

    //Logout
    @POST("/logout")
    Call<User> logoutUser(
            @Header("Authorization") String bearer,
            @Body User user
    );

    // Get details for a tour
    @GET("/tours/{tourId}")
    Call<Tour> getTour(
        @Path("tourId") int tourId
    );

    //Get details for a tour with Auth
    @GET("/tours/{tourId}")
    Call<Tour> getTourWithAuth(
            @Path("tourId") int tourId,
            @Header("Authorization") String bearer
    );

    // Get notifications
    @GET("/notifications")
    Call<List<Notification>> getNotifications(
            @Header("Authorization") String bearer
    );

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
            .addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    String cacheControl = originalResponse.header("Cache-Control");

                    if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                            cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                        return originalResponse.newBuilder()
                                .header("Cache-Control", "public, max-age=" + 10)
                                .build();
                    }
                    return originalResponse;
                }
            })
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    if (!Utils.isNetworkAvailable()) {
                        Log.d(Utils.LOGTAG, "rewriting request");

                        int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                        request = request.newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .build();
                    }

                    return chain.proceed(request);
                }
            })
            .cache(new Cache(new File(getCacheDir(),  "responses"), 10 * 1024 * 1024)) // 10 MiB
            .addInterceptor(loggingInterceptor)
            .build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.114")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();

}
