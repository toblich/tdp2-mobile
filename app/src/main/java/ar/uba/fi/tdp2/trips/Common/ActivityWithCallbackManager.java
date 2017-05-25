package ar.uba.fi.tdp2.trips.Common;

import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;

public abstract class ActivityWithCallbackManager extends AppCompatActivity {
    public CallbackManager callbackManager = CallbackManager.Factory.create();
}
