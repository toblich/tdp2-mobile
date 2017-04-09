package ar.uba.fi.tdp2.trips;

import android.app.SearchManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private List<Attraction> attractions;

    private String locality;
    private Double latitude;
    private Double longitude;
    private boolean inMainWithoutAttractions = false;
    private Context localContext = this;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private RV_AttractionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        locality  = bundle.getString("locality");
        latitude  = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");

        initializeActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initializeData();
    }

    private void initializeData() {
        attractions = new ArrayList<>();

        if (!Utils.isNetworkAvailable(getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Toast.makeText(localContext, "Error: No hay conexión a internet.", Toast.LENGTH_SHORT).show(); // TODO internationalize
            Log.e("TRIPS","Error: No hay conexión a internet");
            return;
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<Attraction>> call  = backendService.getAttractions(latitude, longitude, 1.0);

        call.enqueue(new Callback<List<Attraction>>() {
            @Override
            public void onResponse(Call<List<Attraction>> call, Response<List<Attraction>> response) {
                Log.d("TRIPS", "got attractions: " + response.body().toString());
                attractions = response.body();

                checkChangeLayout();
                adapter = new RV_AttractionAdapter(attractions, localContext);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Attraction>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.cities_search);
        searchItem.getIcon().setColorFilter(getResources().getColor(R.color.toolbarContent), PorterDuff.Mode.SRC_IN);
        SearchView search = (SearchView) searchItem.getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                final List<Attraction> filteredModelList = filter(attractions, query);
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
                adapter.setFilter(attractions);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        return true;
    }

    private List<Attraction> filter(List<Attraction> attractions, String query) {
        query = query.toLowerCase();
        final List<Attraction> filteredModelList = new ArrayList<>();
        for (Attraction attraction : attractions) {
            if (attraction.name.toLowerCase().contains(query)) {
                filteredModelList.add(attraction);
            }
        }
        return filteredModelList;
    }

    private void initializeActivity() {
        setContentView(R.layout.activity_main);
        this.setTitle(locality);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        llm = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(llm);
    }

    private void checkChangeLayout() {
        boolean isAttractionsEmpty = (attractions.size() == 0);

        if (isAttractionsEmpty && !inMainWithoutAttractions) {
            setContentView(R.layout.activity_main_without_attractions);
            this.setTitle(locality);
            inMainWithoutAttractions = true;
            return;
        }

        if (!isAttractionsEmpty && inMainWithoutAttractions) {
            initializeActivity();
            inMainWithoutAttractions = false;
            return;
        }
    }
}
