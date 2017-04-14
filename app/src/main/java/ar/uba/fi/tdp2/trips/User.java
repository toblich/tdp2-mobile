package ar.uba.fi.tdp2.trips;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class User {
    private static User user;
    public int id;
    public String token;
    public @SerializedName("fb_token") String fbToken;
    public @SerializedName("fb_public_profile") boolean fbPublicProfile;
    public @SerializedName("fb_post") boolean fbPost;

    private User(int id, String token, boolean fbPublicProfile, boolean fbPost) {
        this.id     = id;
        this.token  = token;
        this.fbPublicProfile = fbPublicProfile;
        this.fbPost = fbPost;
    }

    private User(String fbToken) {
        this.fbToken = fbToken;
    }

    @Override
    public String toString() {
        return "User {\n  id: " + id + "\n  token: " + token + "\n fbPublicProfile: " + fbPublicProfile + "\n fbPost: " + fbPost + "\n}";
    }

    public interface Callback {
        void onSuccess(User user);
    }

    private static void createFromFbToken(String fbToken,
                                         final SharedPreferences settings,
                                         final Callback callback) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        user = new User(fbToken);
        Call<User> call = backendService.createUser(user);

        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null) {
                    Log.d("TRIPS", "came with response: " + response.toString());
                    return;
                }
                Log.d("TRIPS", "got user: " + response.body().toString());
                user = response.body();
                user.fbPublicProfile = true;
                user.persistUser(settings);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                user = null;
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    public static User getInstance(SharedPreferences settings) {
        if (user == null) {
            user = getPersistedUser(settings);
        }
        return user;
    }

    @Nullable
    private static User getPersistedUser(SharedPreferences settings) {
        int userId = settings.getInt("userId", 0);
        String userToken = settings.getString("userToken", null);
        boolean fbPublicProfile = settings.getBoolean("userFbPublicProfile", false);
        boolean fbPost = settings.getBoolean("userFbPost", false);
        if (userId != 0 && userToken != null) {
            return new User(userId, userToken, fbPublicProfile, fbPost);
        }
        return null;
    }

    private void persistUser(final SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("userId", id);
        editor.putString("userToken", token);
        editor.putBoolean("userFbPublicProfile", fbPublicProfile);
        editor.putBoolean("userFbPost", fbPost);
        editor.commit();
        System.out.println(this);
    }

    private static void _loginWithSocialNetwork(CallbackManager callbackManager,
                                                final SharedPreferences sharedPreferences,
                                                final Callback callback) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println(loginResult.toString());
                String token = loginResult.getAccessToken().getToken();
                System.out.println(token);
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
    }

    public static void loginWithSocialNetwork(Activity activity,
                                              CallbackManager callbackManager,
                                              final SharedPreferences sharedPreferences,
                                              final Callback callback) {
        LoginManager loginManager = LoginManager.getInstance();
        _loginWithSocialNetwork(callbackManager, sharedPreferences, callback);
        loginManager.logInWithReadPermissions(
                activity, Arrays.asList("email", "public_profile"));
    }

    private void _getFbPostPermissions(CallbackManager callbackManager,
                                       final SharedPreferences sharedPreferences,
                                       final Callback callback) {
        LoginManager loginManager = LoginManager.getInstance();
        final User user = this;
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                user.fbPost = true;
                user.persistUser(sharedPreferences);
                callback.onSuccess(user);
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
    }

    public void getFbPostPermissions(Activity activity,
                                     CallbackManager callbackManager,
                                     SharedPreferences sharedPreferences,
                                     final Callback callback) {
        LoginManager loginManager = LoginManager.getInstance();
        _getFbPostPermissions(callbackManager, sharedPreferences, callback);
        loginManager.logInWithPublishPermissions(activity,
                Arrays.asList("publish_actions"));
    }

    public void postInSocialNetwork(final Fragment fragment,
                                    CallbackManager callbackManager,
                                    final String message) {
        OkHttpClient okHttpClient = BackendService.okHttpClient;
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                "{\"text\":\"" + message + "\"}");
        Request request = new Request.Builder()
                .url("https://api.splex.rocks/posts")
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

            }

        };
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
}
