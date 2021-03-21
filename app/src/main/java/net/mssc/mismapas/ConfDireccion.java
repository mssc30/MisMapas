package net.mssc.mismapas;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class ConfDireccion extends AppCompatActivity {

    private static final int REQUEST_UBICACION = 12;
    Button aceptar;
    TextView direccion;
    FusedLocationProviderClient fusedLocationClient;
    SharedPreferences.Editor myEditor;
    SharedPreferences myPreferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_direccion);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();

        aceptar = findViewById(R.id.btnOk);
        direccion = findViewById(R.id.direccion);
        direccion.setText(myPreferences.getString("DESTINO", ""));

        //Instancia del cliente de proveedor de ubicación combinada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        aceptar.setOnClickListener(v -> {
            validarPermiso();
        });
    }

    //VALIDAR PERMISOS DE HUBICACION
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validarPermiso() {

        //SI LOS PERMISOS ESTAN CONCEDIDOS
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //OBTENER LA ULTIMA HUBICACION DEL DISPOSITIVO
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            //GUARDAR EN PREFERENCIAS EL ORIGEN Y EL DESTINO
                            myEditor.putString("ORIGEN", location.getLatitude() + "," + location.getLongitude());
                            myEditor.putString("DESTINO", direccion.getText().toString());
                            myEditor.commit();

                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //SI LA HUBICACION ES NULL PUEDE SER PORQUE EL DISPOSITIVO NO TIENE ENCENDIDA LA HUBICACION, ES NUEVO
                            Toast.makeText(this, "Error al detectar la hubicacion actual", Toast.LENGTH_LONG).show();
                        }
                    });

        //EN CASO DE QUE NO SE HAYA ACEPTADO EL PERMISO, INFORMAR AL USUARIO QUE SON NECESARIOS
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) &&
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Toast.makeText(this, "Es necesario para trazar la ruta", Toast.LENGTH_LONG).show();
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, //especificar permisos
                    REQUEST_UBICACION //variable para identificar la respuesta
            );
            aceptar.setEnabled(false);

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, //especificar permisos
                    REQUEST_UBICACION //variable para identificar la respuesta
            );
        }
    }

    //RECIBE LA RESPUESTA DE LA SOLICITUD DE PERMISOS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //evaluar quien esta resolviendo, porque todas las respuestas de permisos
        //caen en este metodo
        if (requestCode == REQUEST_UBICACION) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //si el permiso fue concedido
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    aceptar.setEnabled(true);
                } else {
                    Toast.makeText(this, "No se ha concedido el permiso", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
