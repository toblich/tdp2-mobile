package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.Manifest;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationManager;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class InitialActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final String LOGTAG = "Trips";
    private GoogleApiClient apiClient;
    private Context context;
    private LocationManager locManager;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        geocoder = new Geocoder(context);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.
        Toast.makeText(context, "Error: No se pudo conectar con Google Play Services", Toast.LENGTH_SHORT).show();
        Log.e(LOGTAG,"Error: No se pudo conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {
            updateLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Toast.makeText(context, "Error: Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();
        Log.e(LOGTAG,"Error: Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                updateLocation();
            } else {
                //Permiso denegado
                Toast.makeText(context,"Error: Permiso de localizacion denegado", Toast.LENGTH_SHORT).show();
                Log.e(LOGTAG,"Error: Permiso de localizacion denegado");
            }
        }
    }

    private void updateLocation() {
        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            @SuppressWarnings("MissingPermission")
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            setLocation(lastLocation);
        } else {
            Toast.makeText(context, "Error: GPS deshabilitado, debe habilitarlo para que el programa funcione", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG,"Error: GPS deshabilitado, debe habilitarlo para que el programa funcione");
        }
    }

    private void setLocation(Location loc) {
        if (loc != null) {
            // Elemento list que contendra la direccion
            List<Address> direcciones = null;
            // Funcion para obtener el nombre desde el geocoder
            try {
                direcciones = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(),1);
            } catch (Exception e) {
                Toast.makeText(context,"Error en geocoder: " + e.toString(), Toast.LENGTH_SHORT).show();
                Log.e(LOGTAG,"Error en geocoder: " + e.toString());
            }
            // Funcion que determina si se obtuvo resultado o no
            if(direcciones != null && direcciones.size() > 0 ) {
                // Creamos el objeto address
                Address direccion = direcciones.get(0);
                // Creamos el string a partir del elemento direccion
                String direccionText = direccion.getLocality() + ", " + direccion.getCountryName();
                SystemClock.sleep(1000);
                Toast.makeText(context, "Usted se encuentra en: " + direccionText, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("ubicacion", direccion.getLocality());
                startActivity(intent);
            } else {
                Toast.makeText(context, "Error: El GPS no pudo establecer su dirección", Toast.LENGTH_SHORT).show();
                Log.e(LOGTAG,"Error: El GPS no pudo establecer su dirección");
            }
        } else {
            Toast.makeText(context, "Error: El GPS no pudo establecer su ubicacion", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG,"Error: El GPS no pudo establecer su ubicacion");
        }
    }
}