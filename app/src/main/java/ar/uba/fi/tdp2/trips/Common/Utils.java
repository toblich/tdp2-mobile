package ar.uba.fi.tdp2.trips.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.SessionActivity;
import ar.uba.fi.tdp2.trips.Cities.InitialActivity;
import ar.uba.fi.tdp2.trips.R;

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

    public static void applySessionToDrawer(final Context context, NavigationView navigationView, User user) {
        MenuItem notificationsMenuItem = navigationView.getMenu().findItem(R.id.nav_notifications);
        MenuItem logInMenuItem = navigationView.getMenu().findItem(R.id.nav_initiate_session);
        MenuItem logOutMenuItem = navigationView.getMenu().findItem(R.id.nav_close_session);

        boolean isLoggedIn = user != null;

        notificationsMenuItem.setVisible(isLoggedIn);
        logInMenuItem.setVisible(!isLoggedIn);
        logOutMenuItem.setVisible(isLoggedIn);

        ImageView profilePic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_pic);

        if (!isLoggedIn || isBlank(user.profilePhotoUri)) {
            profilePic.setVisibility(View.INVISIBLE);
            return;
        }

        profilePic.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(user.profilePhotoUri)
                .dontAnimate()
                .transform(new CircleTransform(context))
                .into(profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.login(context);
            }
        });
    }

    public static void logout(final Activity activity, final NavigationView navigationView, final boolean returnToInitialActivity) {
        Toast.makeText(activity, R.string.logging_out, Toast.LENGTH_SHORT).show();
        User.logout(activity.getSharedPreferences("user", 0), new User.Callback() {
            @Override
            public void onSuccess(User user) {
                Utils.applySessionToDrawer(activity, navigationView, null);
                Toast.makeText(activity, R.string.logged_out, Toast.LENGTH_SHORT).show();
                if (returnToInitialActivity) {
                    activity.navigateUpTo(new Intent(activity, InitialActivity.class));
                }
            }

            @Override
            public void onError(User user) {
                Utils.applySessionToDrawer(activity, navigationView, User.getInstance(activity.getSharedPreferences("user", 0)));
                Toast.makeText(activity, R.string.could_not_log_out, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void login(final Context context) {
        Intent intent = new Intent(context, SessionActivity.class);
        context.startActivity(intent);
    }
}
