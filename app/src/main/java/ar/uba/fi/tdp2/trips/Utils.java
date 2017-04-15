package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static final String LOGTAG = "Trips";

    public static boolean isNetworkAvailable(ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isNetworkAvailable(Object managerAsObject) {
        ConnectivityManager manager = (ConnectivityManager) managerAsObject;
        return isNetworkAvailable(manager);
    }

    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }

    public static boolean isBlank(final String string) {
        return string == null || string.trim().equals("");
    }
}
