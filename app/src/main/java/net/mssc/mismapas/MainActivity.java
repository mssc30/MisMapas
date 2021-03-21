package net.mssc.mismapas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String KEY = "AIzaSyBHtYD_i3eqYqdCroUTQDwzb5FtqD323oc";
    SupportMapFragment mapFragment;
    GoogleMap mapa;
    String ORIGEN = "Montreal";
    String DESTINO;
    SharedPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DESTINO = myPreferences.getString("DIRECCION", "");

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

        /**AGREGAR UN MARCADOR EN LA POSICION 0,0
        mapa.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        // Sets the map type to be "hybrid"
        //mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

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

        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));**/

        peticionRutas();
    }

    private void peticionRutas() {
        //LISTA DE LISTAS DE PUNTOS, ES PARA TRAZAR LA POLILINEA
        final List<List<LatLng>> listaPoli = new ArrayList<>();

        //STRING DE LA PETICION A LA API DE DIRECCIONES
        String URL_PETICION = "https://maps.googleapis.com/maps/api/directions/json?origin=" + ORIGEN +"&destination="+DESTINO+"&key=" + KEY;

        //REQUESTQUEUE PARA REALIZAR LA PETICION UNA VEZ CONSTRUIDA
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //CONSTRUCCION DE LA STRING REQUEST
        //CUANDO RESPONDA
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_PETICION,
                response -> {
                    try {
                        //OBTENER EL OBJETO DE RESPUESTA
                        JSONObject obj = new JSONObject(response);
                        //OBTENER EL ARREGLO DE PASOS DEL RESPONSE
                        JSONArray listaPuntos = obj.getJSONArray("routes")
                                .getJSONObject(0).getJSONArray("legs")
                                .getJSONObject(0).getJSONArray("steps");

                        //POR CADA PASO EN EL RESPONSE, OBTENER EL OBJETO POLYLINE Y EL STRING DE SUS PUNTOS CODIFICADO
                        for (int i = 0; i < listaPuntos.length(); i++) {
                            String puntos = listaPuntos.getJSONObject(i).
                                    getJSONObject("polyline").
                                    getString("points");

                            //AGREGAR LA LISTA DE PUNTOS A LA LISTA DE LISTAS.
                            //SE USA LA API POLYUTIL PARA DECODIFICAR LOS PUNTOS
                            listaPoli.add(PolyUtil.decode(puntos));
                        }

                        //POR CADA LISTA DE PUNTOS EN LA LISTA POLI SE DIBUJA UNA POLILINEA EN EL MAPA
                        //OBTENIENDO LA RUTA COMPLETA
                        for (int i = 0; i < listaPoli.size(); i++) {
                            Log.d("RUTAS", listaPoli.get(i).toString());
                            mapa.addPolyline(new PolylineOptions().addAll(listaPoli.get(i)).color(Color.RED));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //CONFIGURAR MAPA CON MARCADORES
                    mapa.addMarker(new MarkerOptions()
                            .position(listaPoli.get(0).get(0))
                            .title(ORIGEN));

                    mapa.addMarker(new MarkerOptions()
                            .position(listaPoli.get(listaPoli.size()-1).get(listaPoli.get(listaPoli.size()-1).size()-1))
                            .title(DESTINO));

                    //ANIMACION MAS PRO
                    CameraPosition cameraPosition = CameraPosition.builder().
                            target(listaPoli.get(0).get(0))
                            .zoom(12) //ZOOM
                            .tilt(60) //INCLINACION
                            .bearing(90) //DIRECCION DE LA CAMARA EN GRADOS
                            .build();
                    mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                },
                error -> {
                    //MOSTRAR ERROR SI OCURRE
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                });

        //AGREGAR PETICION A LA COLA
        requestQueue.add(stringRequest);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}