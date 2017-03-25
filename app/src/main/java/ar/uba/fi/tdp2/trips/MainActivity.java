package ar.uba.fi.tdp2.trips;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se obtiene el email con el que se inicio sesion/registro
        Bundle bundle = getIntent().getExtras();
        ubicacion = bundle.getString("ubicacion");

        this.setTitle(ubicacion);

    }

    @Override
    public void onBackPressed() {}
}