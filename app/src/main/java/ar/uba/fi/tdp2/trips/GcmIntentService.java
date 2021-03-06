package ar.uba.fi.tdp2.trips;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import ar.uba.fi.tdp2.trips.Cities.InitialActivity;
import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.Notifications.NotificationsActivity;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            sendNotification(extras);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle notification) {
        SharedPreferences prefs = getSharedPreferences("switchCheck", MODE_PRIVATE);
        if (!prefs.getBoolean("isChecked", true)) {
            return;
        }

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String url = notification.getString("url", null);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        String message = notification.getString("message");
        String title = notification.getString("title");

        Intent intent;
        if (Utils.isNotBlank(url)) {
            // Opens the browser with the URL when the notification is taped
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        } else {
            // Opens the application in the NotificationsActivity
            intent = new Intent(this, NotificationsActivity.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification_sound);
        ringtone.play();

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.trips_icon);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.trips_icon)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
