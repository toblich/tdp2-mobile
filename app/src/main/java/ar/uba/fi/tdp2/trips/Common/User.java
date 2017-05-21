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

    private User() {
    }

    @Override
    public String toString() {
        return "User {\n  id: " + id + "\n  token: " + token + "\n fbPublicProfile: " + fbPublicProfile + "\n fbPost: " + fbPost + "\n}";
    }

    public interface Callback {
        void onSuccess(User user);
        void onError(User user);
    }

    public static void logout(Activity activity, final Callback callback) {
        final SharedPreferences settings = getSharedPreferences(activity);
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        user = User.getInstance(settings);
        if (user == null) {
            return;
        }
        String bearer = "Bearer " + user.token;
        Call<User> call = backendService.logoutUser(bearer, user);
        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logOut();
                user.deleteUser(settings);
                callback.onSuccess(response.body());
                user = null;
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.no_server_error, Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
                callback.onError(null);
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
        Log.d("TRIPS", "getting persisted user: " + userId + " " + userToken);
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

    private void deleteUser(final SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("userId");
        editor.remove("userToken");
        editor.remove("userFbPublicProfile");
        editor.remove("userFbPost");
        editor.remove("fbUserId");
        editor.remove("twUserId");
        editor.commit();
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

    private static void createFromFbToken(String fbUserId,
                                          String fbToken,
                                          boolean fbPost,
                                          final SharedPreferences settings,
                                          final Callback callback) {
        Log.d(Utils.LOGTAG, "creating user from fb token");
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        user = User.getInstance(settings);
        if (user == null) {
            user = new User();
        }

        user.fbUserId = fbUserId;
        user.fbToken = fbToken;
        user.fbPost = fbPost;
        user.deviceToken = DeviceToken.getInstance().getDeviceToken();

        Call<User> call = backendService.createUser(user);

        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(Utils.LOGTAG, "onResponse from backend after creating user from fb token");
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
                Toast.makeText(getApplicationContext(), R.string.no_server_error, Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    public static void setFullFbLogin(final Activity activity,
                                      final LoginButton button,
                                      final CallbackManager callbackManager,
                                      final Callback interimCallback,
                                      final Callback callback) {
        Log.d(Utils.LOGTAG, "setting up login in setFullFbLogin");
        button.setReadPermissions(Arrays.asList("email", "public_profile"));
        button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult firstLoginResult) {
                Log.d(Utils.LOGTAG, "onSuccess of first login request");
                System.out.println("firstLoginResult: " + firstLoginResult.toString());
                final String userId = firstLoginResult.getAccessToken().getUserId();
                final String token = firstLoginResult.getAccessToken().getToken();
                System.out.println("token: " + token);

                final LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult secondLoginResult) {
                        Log.d(Utils.LOGTAG, "onSuccess of second request");
                        interimCallback.onSuccess(user);
                        User.createFromFbToken(userId, token, true, User.getSharedPreferences(activity), callback);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(Utils.LOGTAG, "onCancel of second login request");
                        loginManager.logOut();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(Utils.LOGTAG, "2nd login failed with facebook error: " + error.getMessage());
                        interimCallback.onError(null);
                        loginManager.logOut();
                    }
                });
                loginManager.logInWithPublishPermissions(activity, Arrays.asList("publish_actions"));
            }

            @Override
            public void onCancel() {
                Log.d(Utils.LOGTAG, "1st login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Utils.LOGTAG, "1st login failed with facebook error: " + error.getMessage());
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

    private static SharedPreferences getSharedPreferences(Activity activity) {
        return activity.getSharedPreferences("user", 0);
    }
}
