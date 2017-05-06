package ar.uba.fi.tdp2.trips.Common;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static final String LOGTAG = "Trips";
    public static final int NO_POINT_OF_INTEREST = -1;
    private static ConnectivityManager manager;
    private static String shortHoursUnit;
    private static String shortMinutesUnit;
    private static String hoursUnit;
    private static String minutesUnit;
    private static String and;

    public static void setStrings(String hoursUnit, String minutesUnit, String and) {
        Utils.hoursUnit = hoursUnit;
        Utils.minutesUnit = minutesUnit;
        Utils.and = and;
    }

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
        StringBuilder builder = new StringBuilder();

        int hours = time / 60;
        if (hours > 0) {
            builder.append(hours).append(shortHoursUnit);
        }

        int minutes = time % 60;
        if (hours > 0 && minutes > 0) {
            builder.append(" ");
        }

        if (minutes > 0) {
            builder.append(minutes).append(shortMinutesUnit);
        }

        return builder.toString();
    }

    public static String prettyTimeStr(int time) {
        StringBuilder builder = new StringBuilder();

        int hours = time / 60;
        if (hours > 0) {
            builder.append(hours)
                    .append(" ")
                    .append(hoursUnit);
        }

        int minutes = time % 60;
        if (hours > 0 && minutes > 0) {
            builder.append(" ")
                    .append(and)
                    .append(" ");
        }

        if (minutes > 0) {
            builder.append(minutes)
                    .append(" ")
                    .append(minutesUnit);
        }

        return builder.toString();
    }
}