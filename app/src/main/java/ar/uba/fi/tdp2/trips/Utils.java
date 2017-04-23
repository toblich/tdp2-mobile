package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static final String LOGTAG = "Trips";
    private static ConnectivityManager manager;
    private static String shortHoursUnit;
    private static String shortMinutesUnit;

    public static void setShortTimeUnits(String h, String m) {
        Utils.shortHoursUnit = h;
        Utils.shortMinutesUnit = m;
    }

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

    public static String prettyShortTimeStr(int time) {
        int hours = time / 60;
        int minutes = time % 60;

        StringBuilder builder = new StringBuilder();

        if (hours > 0) {
            builder.append(hours).append(shortHoursUnit);
        }

        if (hours > 0 && minutes > 0) {
            builder.append(" ");
        }

        if (minutes > 0) {
            builder.append(minutes).append(shortMinutesUnit);
        }

        return builder.toString();
    }
}
