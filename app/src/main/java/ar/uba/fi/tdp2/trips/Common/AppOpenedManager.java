package ar.uba.fi.tdp2.trips.Common;

import android.content.SharedPreferences;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by agustinsantiago on 5/27/17.
 */

public class AppOpenedManager {
    private int count = 0;
    private String device_token;
    private String country_code;
    private SharedPreferences sharedPreferences;

    public AppOpenedManager(SharedPreferences sharedPreferences_) {
        sharedPreferences = sharedPreferences_;
    }

    public void onDeviceTokenFound(String device_token_) {
        count += 1;
        device_token = device_token_;
        sendAppOpened();
    }

    public void onLocationFound(String country_code_) {
        count += 1;
        country_code = country_code_;
        sendAppOpened();
    }

    public void sendAppOpened() {
        if (count != 2) {
            return;
        }
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        String token;
        User user = User.getInstance(sharedPreferences);
        if (user != null) {
            token = "Bearer " + user.token;
        } else {
            token = null;
        }
        Call<Void> call = backendService.postAppOpened(device_token, country_code, token);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}
