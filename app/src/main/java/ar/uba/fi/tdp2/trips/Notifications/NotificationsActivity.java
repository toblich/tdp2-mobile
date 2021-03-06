package ar.uba.fi.tdp2.trips.Notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.tdp2.trips.Cities.InitialActivity;
import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context localContext = this;
    private List<Notification> notifications;
    private RecyclerView recyclerView;
    private boolean isSwitchChecked;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        this.setTitle(getString(R.string.nav_menu_notifications));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.rvNotifications);
        LinearLayoutManager llm = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(llm);

        //Obtengo el estado del switch
        SharedPreferences prefs = getSharedPreferences("switchCheck", MODE_PRIVATE);
        isSwitchChecked = prefs.getBoolean("isChecked", true);

        getNotificactions();
    }

    private void getNotificactions() {
        notifications = new ArrayList<>();

        if (!Utils.isNetworkAvailable()) {
            Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
            Log.e(Utils.LOGTAG, getString(R.string.no_internet_error));
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        User user = User.getInstance(getSharedPreferences("user", 0));
        String bearer = "Bearer " + user.token;
        Call<List<Notification>> call  = backendService.getNotifications(bearer);

        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.body() == null) {
                    return;
                }
                notifications = response.body();

                RV_NotificationsAdapter adapter = new RV_NotificationsAdapter(notifications, localContext);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_switch, menu);
        MenuItem item = menu.findItem(R.id.switch_notification);
        item.setActionView(R.layout.switch_item);

        SwitchCompat switchCompat = (SwitchCompat) menu.findItem(R.id.switch_notification).getActionView().findViewById(R.id.switch_notification);
        switchCompat.setChecked(isSwitchChecked);
        switchCompat.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Guardo el estado actual del switch
                SharedPreferences.Editor editor = getSharedPreferences("switchCheck", MODE_PRIVATE).edit();
                editor.putBoolean("isChecked", isChecked);
                editor.commit();
            }
        });

        return true;
    }

    @Override
    public void onResume() {
        Utils.applySessionToDrawer(this, navigationView, User.getInstance(getSharedPreferences("user", 0)));
        navigationView.getMenu().findItem(R.id.nav_notifications).setChecked(true);
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_cities:
                Intent intent = new Intent(this, InitialActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_notifications:
                // Do nothing
                break;
            case R.id.nav_close_session:
                Utils.logout(this, navigationView, true);
                break;
            case R.id.nav_initiate_session:
                // This is here just in case. The notifications page should never be
                // visible when the user is logged out but, just in case, the button
                // is functional.
                Utils.login(this);
                break;
            default:
                Log.d(Utils.LOGTAG, "Unknown navigation item selected. Id: " + id);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
