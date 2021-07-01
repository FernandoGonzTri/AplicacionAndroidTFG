package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMedidas extends AppCompatActivity {

    String usuario;
    Button botonAzucar, botonTension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medidas);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");

        botonAzucar = (Button)findViewById(R.id.botonAzucar);

        botonAzucar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMedidas.this, MainAzucar.class);
                i.putExtra("Usuario", usuario);
                startActivity(i);
            }
        });

        botonTension = (Button)findViewById(R.id.botonTension);

        botonTension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMedidas.this, MainTension.class);
                startActivity(i);
            }
        });
    }
}