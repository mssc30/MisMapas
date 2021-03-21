package net.mssc.mismapas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ConfDireccion extends AppCompatActivity {

    Button aceptar;
    TextView direccion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_direccion);

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor myEditor = myPreferences.edit();

        aceptar = findViewById(R.id.btnOk);
        direccion = findViewById(R.id.direccion);

        direccion.setText(myPreferences.getString("DIRECCION", ""));

        aceptar.setOnClickListener(v -> {
            myEditor.putString("DIRECCION", direccion.getText().toString());
            myEditor.commit();

            Intent intent =  new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}
