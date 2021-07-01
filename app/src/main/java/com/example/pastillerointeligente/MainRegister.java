package com.example.pastillerointeligente;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainRegister extends AppCompatActivity {

    Button botonRegister;
    RequestQueue queue;
    ProgressDialog progressDialog;
    String url = "http://pastilleroesp32.ddns.net/ESP32/registrar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        //Se inicializa la cola
        queue = Volley.newRequestQueue(MainRegister.this);
        progressDialog = new ProgressDialog(MainRegister.this);

        botonRegister = (Button)findViewById(R.id.botonRegister);

        botonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Register();
            }
        });
    }

    private void Register(){
        TextView et1 = (TextView)findViewById(R.id.editTextNombre);
        String usuario = et1.getText().toString();
        TextView et2 = (TextView)findViewById(R.id.editTextPassword);
        String password = et2.getText().toString();
        TextView et3 = (TextView)findViewById(R.id.editTextPassword2);
        String repassword = et3.getText().toString();
        TextView et4 = (TextView)findViewById(R.id.editTextEmail);
        String email = et4.getText().toString();
        TextView et5 = (TextView)findViewById(R.id.editTextNumberRef);
        String refPastillero = et5.getText().toString();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Ocultamos el progressDialog

                        try{
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");

                            if(error==true){
                                Toast.makeText(MainRegister.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                //Guardo el usuario en shared preferences
                                SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("Usuario", usuario);
                                editor.putString("Contraseña", password);
                                editor.commit();
                                finish();

                                Toast.makeText(MainRegister.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainRegister.this, MainPastillero.class);
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
                        Toast.makeText(MainRegister.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                protected Map<String, String> getParams(){
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.put("usuario", usuario);
                    parametros.put("password", password);
                    parametros.put("repassword", repassword);
                    parametros.put("email", email);
                    parametros.put("refPastillero", refPastillero);
                    return parametros;
                }
                }   ;

        queue.add(stringRequest);
    }

}