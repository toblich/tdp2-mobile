package ar.uba.fi.tdp2.trips.Common;

import android.util.Log;

/**
 * Created by agustinsantiago on 5/27/17.
 */

public class AppOpenedManager {
    private int count = 0;

    public void onDeviceTokenFound(String device_token) {
        count += 1;
        Log.d("TRIPS", device_token);
        sendAppOpened();
    }

    public void onLocationFound(String country_code) {
        count += 1;
        sendAppOpened();
    }

    public void sendAppOpened() {
        if (count < 2) {
            return;
        }
        Log.d("TRIPS", "SEND TO BACKEND");
        // SEND TO BACKEND
    }
}
