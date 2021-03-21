package net.mssc.mismapas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ADMINISTRADOR DE FRAGMENTOS DE MAP
        //BUSCA EL FRAGMENTO QUE SE LLAMA MAP (ESTA EN EL LAYOUT)
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //OBTIENE EL MAPA DE MANERA ASINCRONA DEFINIENDO COMO CALLBACK THIS
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //LOS MAPAS SE DESCARGAN DE INTERNET HAY UN PROCESO DE ESPERA
        //CUANDO ESTÉ LISTO SE PUEDE INICIALIZAR UN OBJETO MAPA
        mapa = googleMap;

        //AGREGAR UN MARCADOR EN LA POSICION 0,0
        mapa.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        // Sets the map type to be "hybrid"
        mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng sydney = new LatLng(-33.852, 151.211);
        mapa.addMarker(new MarkerOptions().
                position(sydney).title("Marker in Sydney"));

        //ANIMACION PARA QUE EL MAPA SE HUBIQUE EN UN PUNTO. ALGO "BURDA"
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(sydney);
        //mapa.animateCamera(cameraUpdate);

        //ANIMACION MAS PRO
        CameraPosition cameraPosition = CameraPosition.builder().
                target(new LatLng(20.139295, -101.150777))
                .zoom(17) //ZOOM
                .tilt(67) //INCLINACION
                .bearing(90) //DIRECCION DE LA CAMARA EN GRADOS
                .build();

        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Polyline polyline1 = mapa.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}