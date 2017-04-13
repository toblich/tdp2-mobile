package ar.uba.fi.tdp2.trips;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static boolean isNetworkAvailable(ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isNetworkAvailable(Object managerAsObject) {
        ConnectivityManager manager = (ConnectivityManager) managerAsObject;
        return isNetworkAvailable(manager);
    }
}
