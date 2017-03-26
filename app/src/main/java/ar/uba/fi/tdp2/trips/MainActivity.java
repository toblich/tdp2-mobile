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
    // arnold's thumbs up TODO remove this :P
    final String arnoldUrl = "http://3.bp.blogspot.com/-qUH2sD4GWB0/UUn5xBphLjI/AAAAAAAAA2o/MMYWv7n8sNw/s1600/thumb-up-terminator+pablo+M+R.jpg";
    private List<Attraction> attractions;

    private String ubicacion; // TODO pasar a ingles
    private Double latitude;
    private Double longitude;

    private Context localContext = this;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se obtiene la ubicacion del usuario
        Bundle bundle = getIntent().getExtras();
        ubicacion = bundle.getString("ubicacion");
        latitude  = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");

        this.setTitle(ubicacion);

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

//        attractions.add(new Attraction("Obelisco", "Una descripcion corta", arnoldUrl));
//        attractions.add(new Attraction("Usina del Arte", "Una descripción muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy larga.", arnoldUrl));
//        attractions.add(new Attraction("TITULO algomuylargoperpronunciableaverdondelocortaycomo", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
//        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
    }

    @Override
    public void onBackPressed() {}
}