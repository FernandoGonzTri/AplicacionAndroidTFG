package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainAzucar extends AppCompatActivity {

    String usuario;

    Button addAzucar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_azucar);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");

        WebView wv1;
        wv1 = findViewById(R.id.wbAzucar);
        wv1.setWebViewClient(new WebViewClient());
        wv1.loadUrl("http://pastilleroesp32.ddns.net/ESP32/consultazucar.php/?usuario="+usuario);

        addAzucar = (Button)findViewById(R.id.addAzucar);

        addAzucar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainAzucar.this, MainAddAzucar.class);
                i.putExtra("Usuario", usuario);
                startActivity(i);
            }
        });
    }
}