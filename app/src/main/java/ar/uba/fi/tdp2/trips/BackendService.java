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
    @GET("/attractions")
    Call<List<Attraction>> getAttractions(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("radius")    double radius
    );

    @GET("/cities")
    Call<List<City>> getCities();

    @GET("/attractions/2")
    Call<Attraction> getAttraction(
//            @Path("attractionId") int attractionId // TODO
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
