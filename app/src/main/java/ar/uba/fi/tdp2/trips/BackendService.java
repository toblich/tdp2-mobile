package ar.uba.fi.tdp2.trips;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ar.uba.fi.tdp2.trips.AttractionDetails.Review;
import ar.uba.fi.tdp2.trips.Multimedia.Gallery;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
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
    Call<User> createUser(@Body User user);

    // Get details for a tour
    @GET("/tours/{tourId}")
    Call<Tour> getTour(
        @Path("tourId") int tourId
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
            //TODO: IP, ya acomode apiary para que funcione bien sin tener que hardcodear los parámetros.
            //.baseUrl("http://192.168.0.49")
//            .baseUrl("https://private-0e956b-trips5.apiary-mock.com")
            .baseUrl("http://192.168.1.108")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
}
