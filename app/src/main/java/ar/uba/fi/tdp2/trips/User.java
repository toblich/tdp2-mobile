package ar.uba.fi.tdp2.trips;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class User {
    public int id;
    public String token;
    public String fb_token;

    public User(int id, String token) {
        this.id     = id;
        this.token  = token;
    }

    public User(String fb_token) {
        this.fb_token = fb_token;
    }

    @Override
    public String toString() {
        return "User {\n  id: " + id + "\n  token: " + token + "\n}";
    }

    interface Callback {
        void onSuccess(User user);
    }

    public static void createFromFbToken(String fb_token,
                                         final SharedPreferences settings,
                                         final Callback callback) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        User user = new User(fb_token);
        Call<User> call = backendService.createUser(user);

        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("TRIPS", "got user: " + response.body().toString());
                User user = response.body();
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("user_id", user.id);
                editor.putString("user_token", user.token);
                editor.commit();
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    public static User getPersistedUser(SharedPreferences settings) {
        int user_id = settings.getInt("user_id", 0);
        String user_token = settings.getString("user_token", null);
        if (user_id != 0 && user_token != null) {
            return new User(user_id, user_token);
        }
        return null;
    }

    public static void loginWithSocialNetwork(Activity activity,
                                              CallbackManager callbackManager,
                                              final SharedPreferences sharedPreferences,
                                              final Callback callback) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println(loginResult.toString());
                String token = loginResult.getAccessToken().getToken();
                System.out.println(token);
                // Send fb_token to backend

                User.createFromFbToken(token, sharedPreferences, callback);
            }

            @Override
            public void onCancel() {
                // TODO
            }

            @Override
            public void onError(FacebookException error) {
                // TODO
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(
                activity, Arrays.asList("email"));
    }
}
