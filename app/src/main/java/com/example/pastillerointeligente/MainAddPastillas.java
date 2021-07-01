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
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainAddPastillas extends AppCompatActivity {


    String usuario, password;
    Button botonAddPastillas;

    RequestQueue queue;
    ProgressDialog progressDialog;
    String url = "http://pastilleroesp32.ddns.net/ESP32/addpastillas.php";

    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_pastillas);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");
        password=preferences.getString("Contraseña","");

        try {
            conexionMQTT();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //Se inicializa el queue
        queue = Volley.newRequestQueue(MainAddPastillas.this);
        progressDialog = new ProgressDialog(MainAddPastillas.this);

        botonAddPastillas = (Button)findViewById(R.id.botonModificar);

        botonAddPastillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddPastillas();
            }
        });
    }

    private void AddPastillas(){
        TextView et1 = (TextView)findViewById(R.id.etNamePastilla);
        String name_pastilla = et1.getText().toString();

        CheckBox chDes = (CheckBox)findViewById(R.id.chDes);
        String Des=Boolean.toString(chDes.isChecked());

        CheckBox chAlm = (CheckBox)findViewById(R.id.chAlm);
        String Alm=Boolean.toString(chAlm.isChecked());

        CheckBox chMer = (CheckBox)findViewById(R.id.chMer);
        String Mer=Boolean.toString(chMer.isChecked());

        CheckBox chCen = (CheckBox)findViewById(R.id.chCen);
        String Cen=Boolean.toString(chCen.isChecked());

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
                                Toast.makeText(MainAddPastillas.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainAddPastillas.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainAddPastillas.this, MainPastillero.class);
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
                        Toast.makeText(MainAddPastillas.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password",password);
                parametros.put("name_pastilla", name_pastilla);
                parametros.put("desayuno", Des);
                parametros.put("almuerzo", Alm);
                parametros.put("merienda", Mer);
                parametros.put("cena", Cen);
                return parametros;
            }
        }   ;

        queue.add(stringRequest);

        envioAddPastillero(Des,Alm,Mer,Cen);



    }

    private void conexionMQTT() throws MqttException {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://15.188.220.140:1883", clientId);

        MqttConnectOptions connOpts = setUpConnectionOptions(usuario, password);

        client.connect(connOpts);
    }

    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }

    private void envioAddPastillero(String Des, String Alm, String Mer, String Cen){
        String topic = "/ESP32/"+usuario+"/AddPastillas";
        String payload;
        if(Des.equals("true")){
            payload="D";
            try {
                byte[] encodedPayload = new byte[0];
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }
        if(Alm.equals("true")){
            payload="A";
            try {
                byte[] encodedPayload = new byte[0];
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }
        if(Mer.equals("true")){
            payload="M";
            try {
                byte[] encodedPayload = new byte[0];
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }
        if(Cen.equals("true")){
            payload="C";
            try {
                byte[] encodedPayload = new byte[0];
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

    }
}