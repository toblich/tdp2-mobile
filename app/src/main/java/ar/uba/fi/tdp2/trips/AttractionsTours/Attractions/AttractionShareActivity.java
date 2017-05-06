package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AttractionShareActivity extends AppCompatActivity {

    public CallbackManager callbackManager;
    private CharSequence attractionName;
    private TwitterLoginButton loginButton;
    private LoginButton fbLoginButton;

    @Override
    public void onResume() {
        super.onResume();
        processSocialNetworks();
    }

    public void processSocialNetworks() {
        final Activity me = this;
        User user = User.getInstance(getSharedPreferences("user", 0));

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
            } else if(!user.fbPost) {
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

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
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
            loginButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attraction_share);

        Bundle bundle = getIntent().getExtras();
        attractionName = bundle.getCharSequence("attractionName");
        setTitle(getString(R.string.attraction_share_title, attractionName));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callbackManager = CallbackManager.Factory.create();

        final EditText message = (EditText) findViewById(R.id.message);
        message.setText(getString(R.string.attraction_post_message) + " " + attractionName + "!");
    }

    public void cancel(View view) {
        finish();
    }

    public void send(View view) {
        User user = User.getInstance(getSharedPreferences("user", 0));
        System.out.println(user);
        final Button button = (Button) view;
        button.setClickable(false);
        button.setText(R.string.sending);
        if (user != null) {
            final EditText message = (EditText) findViewById(R.id.message);
            final Activity activity = this;
            user.postInSocialNetwork(message.getText().toString(), new User.Callback() {
                @Override
                public void onSuccess(User user) {
                    button.setClickable(true);
                    button.setText(R.string.send);
                    Toast.makeText(activity, R.string.attraction_share_ok,
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onError(User user) {
                    button.setClickable(true);
                    button.setText(R.string.send);
                    Toast.makeText(activity, R.string.attraction_share_error,
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            System.out.println("Error grave");
        }
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText message = (EditText) view.findViewById(R.id.message);
        message.setText(getString(R.string.attraction_post_message) + getArguments().getCharSequence("attractionName") + "!");
        final AttractionShareActivity fragment = this;
        builder.setView(view)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        User user = User.getInstance(getActivity().getSharedPreferences("user", 0));
                        System.out.println(user);
                        if (user != null) {
                            user.postInSocialNetwork(fragment,
                                    fragment.callbackManager,
                                    message.getText().toString());
                        } else {
                            System.out.println("Error grave");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
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
}
