package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import ar.uba.fi.tdp2.trips.Cities.InitialActivity;
import ar.uba.fi.tdp2.trips.Common.CircleTransform;
import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.User;

public class SessionActivity extends AppCompatActivity {

    public CallbackManager callbackManager;
    private TwitterLoginButton twLoginButton;
    private LoginButton fbLoginButton;
    private User user;

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
        user = User.getInstance(getSharedPreferences("user", 0));

        processSessionData();

        fbLogin();
        twLogin();
    }

    private void processSessionData() {
        ImageView profilePic = (ImageView) findViewById(R.id.user_picture);
        TextView sessionsMessage = (TextView) findViewById(R.id.sessions);
        Button closeSessionButton = (Button) findViewById(R.id.logout_button);

        boolean isLoggedIn = user != null;

        if (!isLoggedIn) {
            profilePic.setVisibility(View.INVISIBLE);
            sessionsMessage.setText(R.string.not_logged_in);
            closeSessionButton.setVisibility(View.GONE);
            return;
        }

        boolean withFb = Utils.isNotBlank(user.fbToken);
        boolean withTw = Utils.isNotBlank(user.twToken);

        int sessionsStringCode;
        if (withFb && withTw) {
            sessionsStringCode = R.string.logged_in_fb_tw;
        } else if (withFb) {
            sessionsStringCode = R.string.logged_in_fb;
        } else if (withTw) {
            sessionsStringCode = R.string.logged_in_tw;
        } else {
            // Should never happen
            Log.d(Utils.LOGTAG, "User exists but there is no social network attached to it");
            sessionsStringCode = R.string.not_logged_in;
        }
        sessionsMessage.setText(sessionsStringCode);
        closeSessionButton.setVisibility(View.VISIBLE);

        if (Utils.isBlank(user.profilePhotoUri)) {
            profilePic.setVisibility(View.INVISIBLE);
            return;
        }

        profilePic.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(user.profilePhotoUri)
                .dontAnimate()
                .transform(new CircleTransform(this))
                .into(profilePic);
    }

    public void fbLogin() {
        final Activity me = this;
        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        if (user == null || user.fbUserId == null || !user.fbPublicProfile || !user.fbPost) {
            if (user == null || !user.fbPublicProfile) {
                User.loginWithFacebook(
                        callbackManager,
                        getSharedPreferences("user", 0),
                        new User.Callback() {
                            @Override
                            public void onSuccess(User user) {
                                Toast.makeText(me, R.string.wait_a_second, Toast.LENGTH_LONG).show();
                                User.getFbPostPermissions(me, callbackManager,
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
            fbLoginButton.setVisibility(View.GONE);
        }
    }

    public void twLogin() {
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

        if (user != null && user.twUserId != null) {
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

    public void logout(View view) {
        Toast.makeText(this, R.string.logging_out, Toast.LENGTH_SHORT).show();
        final Context context = this;
        User.logout(getSharedPreferences("user", 0), new User.Callback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(context, R.string.logged_out, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SessionActivity.this, InitialActivity.class);
                NavUtils.navigateUpTo(SessionActivity.this, intent);
            }

            @Override
            public void onError(User user) {
                Toast.makeText(context, R.string.could_not_log_out, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
