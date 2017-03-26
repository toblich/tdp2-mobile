package ar.uba.fi.tdp2.trips;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {
    @GET("/attractions")
    Call<List<Attraction>> getAttractions(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") double radius
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://private-0e956b-trips5.apiary-mock.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
