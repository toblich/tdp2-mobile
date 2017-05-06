package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.User;

public class SessionActivity extends AppCompatActivity {

    public CallbackManager callbackManager;
    private TwitterLoginButton twLoginButton;
    private LoginButton fbLoginButton;

    public static class RequestCode {
        public static final int SHARE = 1;
        public static final int REVIEW = 2;
        public static final int FAVORITE = 3;
        public static final int VISITED = 4;
    }

    @Override
    public void onResume() {
        super.onResume();
        processSocialNetworks();
    }

    public void processSocialNetworks() {
        final Activity me = this;
        User user = User.getInstance(getSharedPreferences("user", 0));

        fbLogin(me, user);
        twLogin(user);
    }

    public void fbLogin(final Activity me, User user) {
        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        if (user == null || user.fbUserId == null || !user.fbPublicProfile || !user.fbPost) {
            findViewById(R.id.fb_logo).setVisibility(View.GONE);
            if (user == null || !user.fbPublicProfile) {
                User.loginWithFacebook(
                        callbackManager,
                        getSharedPreferences("user", 0),
                        new User.Callback() {
                            @Override
                            public void onSuccess(User user) {
                                Toast.makeText(me, R.string.wait_a_second, Toast.LENGTH_LONG).show();
                                user.getFbPostPermissions(me, callbackManager,
                                        getSharedPreferences("user", 0),
                                        new User.Callback() {
                                            @Override
                                            public void onSuccess(User user) {
                                                processSocialNetworks();
                                            }
                                            @Override
                                            public void onError(User user) {}
                                        });
                            }
                            @Override
                            public void onError(User user) {}
                        },
                        fbLoginButton);
            } else if (!user.fbPost) {
                User.postWithFacebook(
                        callbackManager,
                        getSharedPreferences("user", 0),
                        new User.Callback() {
                            @Override
                            public void onSuccess(User user) {
                                processSocialNetworks();
                            }
                            @Override
                            public void onError(User user) {}
                        },
                        fbLoginButton);
            }
        } else {
            findViewById(R.id.fb_logo).setVisibility(View.VISIBLE);
            fbLoginButton.setVisibility(View.GONE);
        }
    }

    public void twLogin(User user) {
        twLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                User.createFromTwToken(
                        String.valueOf(session.getUserId()),
                        session.getAuthToken().token,
                        session.getAuthToken().secret,
                        getSharedPreferences("user", 0),
                        new User.Callback() {
                            @Override
                            public void onSuccess(User user) {
                                processSocialNetworks();
                            }
                            @Override
                            public void onError(User user) {}
                        });
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        if (user == null || user.twUserId == null) {
            findViewById(R.id.tw__twitter_logo).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tw__twitter_logo).setVisibility(View.VISIBLE);
            twLoginButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        setTitle(getString(R.string.manage_sessions));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onDoneButtonClick(View view) {
        finish();
    }
}
