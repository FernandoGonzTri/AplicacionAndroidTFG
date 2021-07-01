package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainConsultaTomas extends AppCompatActivity {

    String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_consulta_tomas);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");

        WebView wv1;
        wv1 = findViewById(R.id.wv1);
        wv1.setWebViewClient(new WebViewClient());
        wv1.loadUrl("http://pastilleroesp32.ddns.net/ESP32/consulta.php/?usuario="+usuario);
    }
}