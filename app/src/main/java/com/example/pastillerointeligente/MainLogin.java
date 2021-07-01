package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainLogin extends AppCompatActivity {

    Button botonLogin;
    RequestQueue queue;
    ProgressDialog progressDialog;
    String url = "http://pastilleroesp32.ddns.net/ESP32/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        //Se inicializa el queue
        queue = Volley.newRequestQueue(MainLogin.this);
        progressDialog = new ProgressDialog(MainLogin.this);

        botonLogin = (Button)findViewById(R.id.botonLogin);

        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void Login(){
        TextView et1 = (TextView)findViewById(R.id.editTextUserLogin);
        String usuario = et1.getText().toString();
        TextView et2 = (TextView)findViewById(R.id.editTextPassLogin);
        String password = et2.getText().toString();




        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Ocultamos el progressDialog

                        try{
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");

                            if(error){
                                Toast.makeText(MainLogin.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                //Guardo el usuario en shared preferences
                                SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("Usuario", usuario);
                                editor.putString("Contraseña", password);
                                editor.commit();
                                finish();

                                Toast.makeText(MainLogin.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainLogin.this, MainPastillero.class);
                                i.putExtra("Usuario", usuario);
                                startActivity(i);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainLogin.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usuario);
                parametros.put("password", password);
                return parametros;
            }
        }   ;

        queue.add(stringRequest);
    }
}