package ar.uba.fi.tdp2.trips.Common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Arrays;

import ar.uba.fi.tdp2.trips.DeviceToken;
import ar.uba.fi.tdp2.trips.R;
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
    public @SerializedName("facebook_id") String fbUserId;
    public @SerializedName("fb_token") String fbToken;
    public @SerializedName("fb_public_profile") boolean fbPublicProfile;
    public @SerializedName("fb_post") boolean fbPost;
    public @SerializedName("twitter_id") String twUserId;
    public @SerializedName("tw_token") String twToken;
    public @SerializedName("tw_secret") String twSecret;
    public @SerializedName("device_token") String deviceToken;
    public @SerializedName("profile_image") String profilePhotoUri;

    private User(int id, String token, boolean fbPublicProfile, boolean fbPost) {
        this.id     = id;
        this.token  = token;
        this.fbPublicProfile = fbPublicProfile;
        this.fbPost = fbPost;
    }

    private User(String fbUserId, String fbToken) {
        this.fbUserId = fbUserId;
        this.fbToken = fbToken;
    }

    private User() {
    }

    @Override
    public String toString() {
        String photo = profilePhotoUri == null ? "" : "\n  profilePhotoUri: " + profilePhotoUri;
        return "User {\n  id: " + id + "\n  token: " + token + "\n fbPublicProfile: " + fbPublicProfile + "\n fbPost: " + fbPost + "\n}";
    }

    public interface Callback {
        void onSuccess(User user);
        void onError(User user);
    }

    private static void createFromFbToken(String fbUserId,
                                          String fbToken,
                                          final SharedPreferences settings,
                                          final Callback callback) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        user = User.getInstance(settings);
        if (user == null) {
            user = new User();
        }

        user.fbUserId = fbUserId;
        user.fbToken = fbToken;
        user.deviceToken = DeviceToken.getInstance().getDeviceToken();

        Call<User> call = backendService.createUser(user);

        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null) {
                    return;
                }
                user.id = response.body().id;
                user.token = response.body().token;
                user.fbPublicProfile = true;
                user.profilePhotoUri = response.body().profilePhotoUri;
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

    public static void createFromTwToken(String twUserId,
                                          String twToken,
                                          String twSecret,
                                          final SharedPreferences settings,
                                          final Callback callback) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        user = User.getInstance(settings);
        if (user == null) {
            user = new User();
        }
        user.twUserId = twUserId;
        user.twToken = twToken;
        user.twSecret = twSecret;
        user.deviceToken = DeviceToken.getInstance().getDeviceToken();

        Call<User> call = backendService.createUser(user);

        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null) {
                    return;
                }
                user.id = response.body().id;
                user.token = response.body().token;
                user.profilePhotoUri = response.body().profilePhotoUri;
                Log.d("TRIPS", "got user: " + response.body().toString());
                user.persistUser(settings);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                user = null;
                t.printStackTrace();
                Context context = getApplicationContext();
                Toast.makeText(context, context.getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
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
        String fbUserId = settings.getString("fbUserId", null);
        String twUserId = settings.getString("twUserId", null);
        String profilePhotoUri = settings.getString("profilePhotoUri", null);
        Log.d("TRIPS", userId + " " + userToken);
        if (userId != 0 && userToken != null) {
            User user = new User();
            user.id = userId;
            user.token = userToken;
            user.fbPublicProfile = fbPublicProfile;
            user.fbPost = fbPost;
            user.fbUserId = fbUserId;
            user.twUserId = twUserId;
            user.deviceToken = DeviceToken.getInstance().getDeviceToken();
            user.profilePhotoUri = profilePhotoUri;
            return user;
        }
        return null;
    }

    private void persistUser(final SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("userId", id);
        editor.putString("userToken", token);
        editor.putBoolean("userFbPublicProfile", fbPublicProfile);
        editor.putBoolean("userFbPost", fbPost);
        editor.putString("fbUserId", fbUserId);
        editor.putString("twUserId", twUserId);
        editor.putString("profilePhotoUri", profilePhotoUri);
        editor.commit();
        System.out.println(this);
    }
//
//    private static void _loginWithSocialNetwork(CallbackManager callbackManager,
//                                                final SharedPreferences sharedPreferences,
//                                                final Callback callback) {
//        LoginManager loginManager = LoginManager.getInstance();
//
//    }

    public static void loginWithSocialNetwork(Activity activity,
                                               CallbackManager callbackManager,
                                               final SharedPreferences sharedPreferences,
                                               final Callback callback) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println(loginResult.toString());
                String userId = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                System.out.println(token);
                User.createFromFbToken(userId, token, sharedPreferences, callback);
            }

            @Override
            public void onCancel() {
                Log.d(Utils.LOGTAG, "Login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Utils.LOGTAG, "Login failed");
            }
        });
        loginManager.logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
    }

    public static void loginWithFacebook(CallbackManager callbackManager,
                                         final SharedPreferences sharedPreferences,
                                         final Callback callback,
                                         LoginButton loginButton) {
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println(loginResult.toString());
                String userId = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                System.out.println(token);
                User.createFromFbToken(userId, token, sharedPreferences, callback);
            }

            @Override
            public void onCancel() {
                Log.d(Utils.LOGTAG, "Login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Utils.LOGTAG, "Login failed");
            }
        });
    }

    public static void postWithFacebook(CallbackManager callbackManager,
                                         final SharedPreferences sharedPreferences,
                                         final Callback callback,
                                         LoginButton loginButton) {
        loginButton.setPublishPermissions(Arrays.asList("publish_actions"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                user.fbPost = true;
                user.persistUser(sharedPreferences);
                callback.onSuccess(user);
            }

            @Override
            public void onCancel() {
                Log.d(Utils.LOGTAG, "Login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Utils.LOGTAG, "Login failed");
            }
        });
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

    public void postInSocialNetwork(final String message, final Callback callback) {
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
        final User me = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        okhttp3.Callback httpCallback = new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(me);
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(me);
                    }
                });
            }

        };
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(httpCallback);
    }
}
