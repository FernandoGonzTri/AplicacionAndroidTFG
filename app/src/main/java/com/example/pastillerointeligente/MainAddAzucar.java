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

public class MainAddAzucar extends AppCompatActivity {

    String usuario, password;
    Button botonAddAzucar;

    RequestQueue queue;
    ProgressDialog progressDialog;
    String url = "http://pastilleroesp32.ddns.net/ESP32/addazucar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_azucar);
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");
        password=preferences.getString("Contraseña","");

        //Se inicializa el queue
        queue = Volley.newRequestQueue(MainAddAzucar.this);
        progressDialog = new ProgressDialog(MainAddAzucar.this);

        botonAddAzucar = (Button)findViewById(R.id.botonAddAzucar);

        botonAddAzucar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddAzucar();
            }
        });
    }

    private void AddAzucar(){
        TextView et1 = (TextView)findViewById(R.id.azucar);
        String azucar = et1.getText().toString();

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
                                Toast.makeText(MainAddAzucar.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainAddAzucar.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainAddAzucar.this, MainPastillero.class);
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
                        Toast.makeText(MainAddAzucar.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password",password);
                parametros.put("azucar",azucar);

                return parametros;
            }
        }   ;

        queue.add(stringRequest);

    }
}