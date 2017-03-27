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
    private boolean inMainWithoutAttractions = false;
    private Context localContext = this;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;

    //TODO: Debug para probar cambio de pantallas varias veces
    //private boolean changeLayout = false;

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

    private void initializeActivity() {
        setContentView(R.layout.activity_main);
        this.setTitle(locality);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        llm = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(llm);
    }

    private void checkChangeLayout() {
        //TODO: para probar que funciona lo del cambio cuando no hay atracciones y despues se agrega alguna.
        //if (!changeLayout) {
        //    attractions = new ArrayList<>();
        //    changeLayout = true;
        //}
        //-------------------------------------------------------------------------------------------------
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
            //TODO: Debug para probar cambio de pantallas cuando no hay atracciones
            //changeLayout = false;
            return;
        }
    }

    @Override
    public void onBackPressed() {}
}
