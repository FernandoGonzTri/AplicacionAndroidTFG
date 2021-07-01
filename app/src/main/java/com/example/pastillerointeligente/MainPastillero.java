package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainPastillero extends AppCompatActivity {

    String usuario;
    TextView tvuser;
    Button botonVincular, botonCerrar, botonComprobar, botonAñadir, botonConsultar, botonMedidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pastillero);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");


        tvuser = (TextView)findViewById(R.id.textViewUser);
        tvuser.setText("Usuario: "+ usuario);



        botonVincular = (Button)findViewById(R.id.botonVincular);

        botonVincular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPastillero.this, MainDispositivo.class);
                i.putExtra("Usuario", usuario);
                startActivity(i);
            }
        });

        botonComprobar = (Button)findViewById(R.id.boton_comprobar);

        botonComprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPastillero.this, MainConsultaTomas.class);
                startActivity(i);
            }
        });

        botonAñadir = (Button)findViewById(R.id.boton_añadir);

        botonAñadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPastillero.this, MainAddPastillas.class);
                startActivity(i);
            }
        });

        botonConsultar = (Button)findViewById(R.id.boton_consultar);

        botonConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPastillero.this, MainConsultaPastillas.class);
                startActivity(i);
            }
        });

        botonMedidas = (Button)findViewById(R.id.botonMedidas);

        botonMedidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPastillero.this, MainMedidas.class);
                startActivity(i);
            }
        });



        botonCerrar = (Button)findViewById(R.id.botonCerrarSesion);

        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Usuario", "");
                editor.commit();
                finish();

                Intent i = new Intent(MainPastillero.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}