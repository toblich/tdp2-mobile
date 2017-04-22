package ar.uba.fi.tdp2.trips;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static final String LOGTAG = "Trips";
<<<<<<< HEAD
    private static ConnectivityManager manager;
    private static String shortHoursUnit;
    private static String shortMinutesUnit;
=======
    private static String hoursUnit;
    private static String minutesUnit;
    private static String and;

    public static void setStrings(String hoursUnit, String minutesUnit, String and) {
        Utils.hoursUnit = hoursUnit;
        Utils.minutesUnit = minutesUnit;
        Utils.and = and;
    }
>>>>>>> Pretty time strings in tour & attraction details

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

<<<<<<< HEAD
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

=======
    public static String prettyTimeStr(int time) {
        int hours = time / 60;
        int minutes = time % 60;
        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours)
                    .append(" ")
                    .append(hoursUnit);
        }
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
>>>>>>> Pretty time strings in tour & attraction details
        return builder.toString();
    }
}
