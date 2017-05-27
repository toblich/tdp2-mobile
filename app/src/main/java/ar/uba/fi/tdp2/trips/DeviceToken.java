package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import ar.uba.fi.tdp2.trips.Common.AppOpenedManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DeviceToken {

    private static DeviceToken mInstance= null;

    private static String deviceToken = "invalid_token";

    protected DeviceToken(){
    }

    public static synchronized DeviceToken getInstance(){
        if(mInstance == null){
            mInstance = new DeviceToken();
        }
        return mInstance;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public static void initializeDeviceToken(final AppOpenedManager appOpenedManager) {
        new Thread(new Runnable() {
            public void run() {
                final Context context = getApplicationContext();
                InstanceID myID = InstanceID.getInstance(context);
                try {
                    deviceToken = myID.getToken(
                            context.getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                            null);
                    appOpenedManager.onDeviceTokenFound(deviceToken);
                    Log.d("device token: ", deviceToken);
                } catch (Exception e) {
                    Log.e("device token: ", "Couldn't get the device token");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
