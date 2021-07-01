package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainAddTension extends AppCompatActivity {

    String usuario, password;
    Button botonEnvioTension;

    RequestQueue queue;
    ProgressDialog progressDialog;
    String url = "http://pastilleroesp32.ddns.net/ESP32/addtension.php";

    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_tension);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");
        password=preferences.getString("Contraseña","");

        //Se inicializa el queue
        queue = Volley.newRequestQueue(MainAddTension.this);
        progressDialog = new ProgressDialog(MainAddTension.this);

        botonEnvioTension = (Button)findViewById(R.id.botonEnvioTension);

        botonEnvioTension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddTension();
            }
        });
    }

    private void AddTension(){
        TextView et1 = (TextView)findViewById(R.id.tension_min);
        String tension_min = et1.getText().toString();

        TextView et2 = (TextView)findViewById(R.id.tension_max);
        String tension_max = et2.getText().toString();

        TextView et3 = (TextView)findViewById(R.id.pulsaciones);
        String puls = et3.getText().toString();



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
                                Toast.makeText(MainAddTension.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainAddTension.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainAddTension.this, MainPastillero.class);
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
                        Toast.makeText(MainAddTension.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password",password);
                parametros.put("tension_min", tension_min);
                parametros.put("tension_max", tension_max);
                parametros.put("puls", puls);

                return parametros;
            }
        }   ;

        queue.add(stringRequest);



    }
}