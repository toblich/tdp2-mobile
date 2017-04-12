package ar.uba.fi.tdp2.trips;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.Manifest;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationManager;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitialActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int LOCATION_PERMISSION_PETITION = 101;
    private static final String LOGTAG = "Trips";
    private GoogleApiClient apiClient;
    private Context context;
    private LocationManager locManager;
    private Geocoder geocoder;
    private CardView geolocalizationCard;
    private Context localContext = this;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private List<City> cities;
    RV_CitiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        this.setTitle(R.string.choose_location);

        context    = getApplicationContext();
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder   = new Geocoder(context);
        geolocalizationCard = (CardView) findViewById(R.id.geolocalization_card);
        geolocalizationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rvCities);
        llm = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(llm);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
    }

    private void initializeData() {
        cities = new ArrayList<>();

        if (!Utils.isNetworkAvailable(getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Toast.makeText(localContext, "Error: No hay conexión a internet.", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e("TRIPS","Error: No hay conexión a internet");
            return;
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<City>> call  = backendService.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                Log.d("TRIPS", "got cities: " + response.body().toString());
                cities = response.body();

                adapter = new RV_CitiesAdapter(cities, localContext);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(context, "Error: No se pudo conectar con Google Play Services", Toast.LENGTH_SHORT).show(); // TODO internationalize
        Log.e(LOGTAG,"Error: No se pudo conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int locationPermissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_PETITION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Toast.makeText(context, "Error: Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show(); // TODO internationalize
        Log.e(LOGTAG,"Error: Se ha interrumpido la conexión con Google Play Services");
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
            Toast.makeText(context,"Error: Permiso de localizacion denegado", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e(LOGTAG,"Error: Permiso de localizacion denegado");
        }
    }

    private void updateLocation() {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "Error: GPS deshabilitado, debe habilitarlo para que el programa funcione", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e(LOGTAG,"Error: GPS deshabilitado, debe habilitarlo para que el programa funcione");
            return;
        }

        if (!Utils.isNetworkAvailable(getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Toast.makeText(context, "Error: No hay conexión a internet.", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e(LOGTAG,"Error: No hay conexión a internet");
            return;
        }

        @SuppressWarnings("MissingPermission")
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        setLocation(lastLocation);
    }

    private void setLocation(Location loc) {
        if (loc == null) {
            Toast.makeText(context, "Error: El GPS no pudo establecer su ubicacion", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e(LOGTAG,"Error: El GPS no pudo establecer su ubicacion");
            return;
        }

        List<Address> addresses = null;

        // Get locality name from geocoder
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(),1);
        } catch (Exception e) {
            Toast.makeText(context,"Error en geocoder: " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG,"Error en geocoder: " + e.toString());
        }

        // Check if successfully got the address
        if(addresses == null || addresses.size() == 0 ) {
            Toast.makeText(context, "Error: El GPS no pudo establecer su dirección", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e(LOGTAG,"Error: El GPS no pudo establecer su dirección");
            return;
        }

        Address address = addresses.get(0);
        String addressText = address.getLocality() + ", " + address.getCountryName();
        Toast.makeText(context, "Usted se encuentra en: " + addressText, Toast.LENGTH_SHORT).show(); // TODO internationalize

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("locality", address.getLocality());
        intent.putExtra("latitude", loc.getLatitude());
        intent.putExtra("longitude", loc.getLongitude());
        startActivity(intent);
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
                        adapter.setFilter(cities);
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
