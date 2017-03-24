package ar.uba.fi.tdp2.trips;

import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // arnold's thumbs up
    final String arnoldUrl = "http://3.bp.blogspot.com/-qUH2sD4GWB0/UUn5xBphLjI/AAAAAAAAA2o/MMYWv7n8sNw/s1600/thumb-up-terminator+pablo+M+R.jpg";
    private List<Attraction> attractions;

    private String ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se obtiene el email con el que se inicio sesion/registro
        Bundle bundle = getIntent().getExtras();
        ubicacion = bundle.getString("ubicacion");

        this.setTitle(ubicacion);

        Context context = this; // TODO verify this is right

        initializeData();

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        RV_AttractionAdapter adapter = new RV_AttractionAdapter(attractions, context);
        rv.setAdapter(adapter);
    }



    // This method creates an ArrayList that has three Person objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeData() {
        attractions = new ArrayList<>();
        attractions.add(new Attraction("Obelisco", "Una descripcion corta", arnoldUrl));
        attractions.add(new Attraction("Usina del Arte", "Una descripción muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy muy larga.", arnoldUrl));
        attractions.add(new Attraction("Casa Rosada", "Alguna descripción más...", arnoldUrl));
    }

    @Override
    public void onBackPressed() {}
}