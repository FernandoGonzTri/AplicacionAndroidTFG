package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button boton1, boton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Comprobar si ya se ha logeado anteriormente
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        if(preferences.getString("Usuario","")!=""){
            Intent i = new Intent(MainActivity.this, MainPastillero.class);
            startActivity(i);
        }

        boton1 = (Button)findViewById(R.id.button);
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainLogin.class);
                startActivity(i);
            }
        });

        boton2 = (Button)findViewById(R.id.button2);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(MainActivity.this, MainRegister.class);
                startActivity(main);
            }
        });
    }


}