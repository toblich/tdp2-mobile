package ar.uba.fi.tdp2.trips;

import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        //Se obtiene la locality del usuario
        Bundle bundle = getIntent().getExtras();
        locality = bundle.getString("locality");
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

    private void initializeData() { // TODO change Volley for retorfit2 or equivalent
        attractions = new ArrayList<>();
        RequestQueue reqQueue = Volley.newRequestQueue(this);

        // TODO put here right URI to API
        String apiari = "https://private-0e956b-trips5.apiary-mock.com/attractions?radius=1.0&longitude=" + longitude.toString() + "&latitude=" + latitude.toString();
        String uri = "http://192.168.0.138" + "/attractions?latitude=" + latitude.toString() + "&longitude=" + longitude.toString() + "&radius=1.0";
        System.out.println(uri);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiari, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0, size = response.length(); i < size; i++) {
                    System.out.println("Processing a response object");
                    try {
                        JSONObject json = response.getJSONObject(i);
                        String name        = json.getString("name");
                        String description = json.getString("description");
                        String photoUrl    = json.has("portrait_image") ? json.getString("portrait_image") : "";

                        attractions.add(new Attraction(name, description, photoUrl));
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                        Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
                        Log.d("TRIPS", exception.toString());
                    }
                }
                RV_AttractionAdapter adapter = new RV_AttractionAdapter(attractions, localContext);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("TRIPS", error.toString());
            }
        });

        reqQueue.add(jsonArrayRequest);
    }

    @Override
    public void onBackPressed() {}
}