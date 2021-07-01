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

public class MainTension extends AppCompatActivity {

    String usuario;

    Button addtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tension);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");

        WebView wv1;
        wv1 = findViewById(R.id.wbtension);
        wv1.setWebViewClient(new WebViewClient());
        wv1.loadUrl("http://pastilleroesp32.ddns.net/ESP32/consultatension.php/?usuario="+usuario);

        addtension = (Button)findViewById(R.id.addtension);

        addtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainTension.this, MainAddTension.class);
                i.putExtra("Usuario", usuario);
                startActivity(i);
            }
        });
    }
}