package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static final String LOGTAG = "Trips";
    private static ConnectivityManager manager;

    public static void setConnectivityManager(Object manager) {
        Utils.manager = (ConnectivityManager) manager;
    }

    public static boolean isNetworkAvailable() {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }

    public static boolean isBlank(final String string) {
        return string == null || string.trim().equals("");
    }
}
