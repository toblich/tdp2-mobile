package ar.uba.fi.tdp2.trips;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {
    //Get the list of all the cities
    @GET("/cities")
    Call<List<City>> getCities();

    //Get the list of the nearest attractions
    @GET("/attractions")
    Call<List<Attraction>> getAttractions(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("radius")    double radius
    );

    //Get the details of a specific attraction
    @GET("/attractions/2")
    Call<Attraction> getAttraction(
//          @Path("attractionId") int attractionId // TODO
    );

    //Get the points of interest's list of a specific attraction
    @GET("/attractions/50/point_of_interests")
    Call<List<PointOfInterest>> getPointsOfInterest(
//          @Path("attractionId") int attractionId // TODO
    );

    //Get the details of a specific point of interest
    @GET("/attractions/50/point_of_interests/1")
    Call<PointOfInterest> getPointOfInterest(
//            @Path("attractionId") int attractionId // TODO
//            @Path("poiId") int poiId //TODO
    );

    OkHttpClient okHttpClient = (new OkHttpClient.Builder())
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request request = original.newBuilder()
                            .header("Accept-Language", Locale.getDefault().getLanguage())
                            .build();

                    Log.d("TRIPS", "Outgoing httpRequest: " + request.toString()
                            + " Headers: " + request.headers().toString());

                    return chain.proceed(request);
                }
            }).build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://private-0e956b-trips5.apiary-mock.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
}
