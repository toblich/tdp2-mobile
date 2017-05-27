package ar.uba.fi.tdp2.trips.Cities;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.SessionActivity;
import ar.uba.fi.tdp2.trips.Common.AppOpenedManager;
import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.CircleTransform;
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.Notifications.NotificationsActivity;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.Utils;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ar.uba.fi.tdp2.trips.AttractionsTours.AttractionsToursTabsActivity;
import ar.uba.fi.tdp2.trips.DeviceToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitialActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ibKtBJe8a3Tjgm6Z9vwsdHbL5";
    private static final String TWITTER_SECRET = "3m7WkvMIvrlEMuFnfJLhNfbkqR633iyRhcsf6G2TI6pdD0t9kF";

    private static final int LOCATION_PERMISSION_PETITION = 101;
    private GoogleApiClient apiClient;
    private LocationManager locManager;
    private Geocoder geocoder;
    private CardView geolocalizationCard;
    private Context localContext = this;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private List<City> cities;
    RV_CitiesAdapter adapter;
    NavigationView navigationView;
    private AppOpenedManager appOpenedManager = new AppOpenedManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_initial);
        this.setTitle(R.string.choose_location);

        //Menu Navigation Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Cosas de estilo y conectividad
        Utils.setConnectivityManager(getSystemService(Context.CONNECTIVITY_SERVICE));
        Utils.setShortTimeUnits(getString(R.string.short_hours), getString(R.string.short_minutes));
        Utils.setStrings(getString(R.string.hours_unit), getString(R.string.minutesUnit), getString(R.string.and));

        //Geolocalizacion
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder   = new Geocoder(localContext);
        geolocalizationCard = (CardView) findViewById(R.id.geolocalization_card);
        geolocalizationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation(true, new SetLocationCallback() {
                    public void run(Activity activity, Location loc, Address address) {
                        appOpenedManager.onLocationFound(address.getCountryCode());
                        Intent intent = new Intent(activity, AttractionsToursTabsActivity.class);
                        intent.putExtra("locality", address.getLocality());
                        intent.putExtra("latitude", loc.getLatitude());
                        intent.putExtra("longitude", loc.getLongitude());
                        startActivity(intent);
                    }
                });
            }
        });

        //Rv para la lista de ciudades
        recyclerView = (RecyclerView) findViewById(R.id.rvCities);
        llm = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(llm);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();



        initializeData();
    }

    @Override
    public void onResume() {
        Utils.applySessionToDrawer(this, navigationView, User.getInstance(getSharedPreferences("user", 0)));
        navigationView.getMenu().findItem(R.id.nav_cities).setChecked(true);
        super.onResume();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_cities:
                // Do nothing
                break;
            case R.id.nav_notifications:
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_close_session:
                Utils.logout(this, navigationView, false);
                break;
            case R.id.nav_initiate_session:
                Utils.login(this);
                break;
            default:
                Log.d(Utils.LOGTAG, "Unknown navigation item selected. Id: " + id);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeData() {
        DeviceToken.getInstance().initializeDeviceToken(appOpenedManager);
        cities = new ArrayList<>();

        if (!Utils.isNetworkAvailable()) {
            Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
            Log.e(Utils.LOGTAG, getString(R.string.no_internet_error));
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<City>> call  = backendService.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.body() == null) {
                    return;
                }

                cities = response.body();

                adapter = new RV_CitiesAdapter(cities, localContext);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(localContext, getString(R.string.no_google_play_services_error), Toast.LENGTH_SHORT).show();
        Log.e(Utils.LOGTAG, getString(R.string.no_google_play_services_error));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int locationPermissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_PETITION);
        }

        // SEND LOCATION TO BACKEND
        updateLocation(false, new SetLocationCallback() {
            public void run(Activity activity, Location loc, Address address) {
                appOpenedManager.onLocationFound(address.getCountryCode());
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Toast.makeText(localContext, getString(R.string.interrupted_google_play_services_error), Toast.LENGTH_SHORT).show();
        Log.e(Utils.LOGTAG, getString(R.string.interrupted_google_play_services_error));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_PETITION) {
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            return;
        } else {
            // Permission denied
            Toast.makeText(localContext, getString(R.string.location_permittion_denied), Toast.LENGTH_SHORT).show();
            Log.e(Utils.LOGTAG, getString(R.string.location_permittion_denied));
        }
    }

    private interface SetLocationCallback {
        void run(Activity activity, Location loc, Address address);
    }

    private void updateLocation(boolean alerts, SetLocationCallback callback) {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (alerts) {
                Toast.makeText(localContext, getString(R.string.no_gps_error), Toast.LENGTH_SHORT).show();
            }
            Log.e(Utils.LOGTAG, getString(R.string.no_gps_error));
            return;
        }
        if (!Utils.isNetworkAvailable()) {
            if (alerts) {
                Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
            }
            Log.e(Utils.LOGTAG, getString(R.string.no_internet_error));
            return;
        }
        @SuppressWarnings("MissingPermission")
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        setLocation(alerts, lastLocation, callback);
    }

    private void setLocation(boolean alerts, Location loc, SetLocationCallback callback) {
        if (loc == null) {
            if (alerts) {
                Toast.makeText(localContext, getString(R.string.no_location_error), Toast.LENGTH_SHORT).show();
            }
            Log.e(Utils.LOGTAG, getString(R.string.no_location_error));

            return;
        }

        List<Address> addresses = null;
        // Get locality name from geocoder
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(),1);
        } catch (Exception e) {
            if (alerts) {
                Toast.makeText(localContext, getString(R.string.geocoder_error) + e.toString(), Toast.LENGTH_SHORT).show();
            }
            Log.e(Utils.LOGTAG, getString(R.string.geocoder_error) + e.toString());
        }
        // Check if successfully got the address
        if(addresses == null || addresses.size() == 0 ) {
            if (alerts) {
                Toast.makeText(localContext, getString(R.string.no_address_error), Toast.LENGTH_SHORT).show();
            }
            Log.e(Utils.LOGTAG, getString(R.string.no_address_error));
            return;
        }

        Address address = addresses.get(0);
        String addressText = address.getLocality() + ", " + address.getCountryName();
        address.getCountryCode();
        if (alerts) {
            Toast.makeText(localContext, getString(R.string.location_found) + " " + addressText, Toast.LENGTH_SHORT).show();
        }

        callback.run(this, loc, address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.getIcon().setColorFilter(getResources().getColor(R.color.toolbarContent), PorterDuff.Mode.SRC_IN);
        SearchView search = (SearchView) searchItem.getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                final List<City> filteredModelList = filter(cities, query);
                if (adapter != null) {
                    adapter.setFilter(filteredModelList);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        if (adapter != null && cities != null) {
                            adapter.setFilter(cities);
                        }
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });

        return true;
    }

    private List<City> filter(List<City> citiesList, String query) {
        query = query.toLowerCase();

        final List<City> filteredModelList = new ArrayList<>();
        for (City city : citiesList) {
            final String cityName = city.getName().toLowerCase();
            final String cityCountry = city.getCountry().toLowerCase();
            if (cityName.contains(query) || cityCountry.contains(query)) {
                filteredModelList.add(city);
            }
        }
        return filteredModelList;
    }
}
