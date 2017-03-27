package ar.uba.fi.tdp2.trips;

import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private Context localContext = this;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        locality  = bundle.getString("locality");
        latitude  = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");

        this.setTitle(locality);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(llm);
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
                RV_AttractionAdapter adapter = new RV_AttractionAdapter(attractions, localContext);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Attraction>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                Log.d("TRIPS", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {}
}