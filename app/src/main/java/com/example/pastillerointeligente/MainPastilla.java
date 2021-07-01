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
import android.widget.EditText;
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

public class MainPastilla extends AppCompatActivity {

    EditText name_pastilla;
    RequestQueue queue;
    ProgressDialog progressDialog;
    String url1 = "http://pastilleroesp32.ddns.net/ESP32/consultapastilla.php";
    String url2 = "http://pastilleroesp32.ddns.net/ESP32/modificarpastilla.php";
    String url3 = "http://pastilleroesp32.ddns.net/ESP32/eliminarpastilla.php";

    MqttAndroidClient client;

    String usuario, password;

    Button botonModificar, botonEliminar;

    Boolean desAnt, almAnt, merAnt, cenAnt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pastilla);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");
        password=preferences.getString("Contraseña","");

        try {
            conexionMQTT();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //Se inicializa el queue
        queue = Volley.newRequestQueue(MainPastilla.this);
        progressDialog = new ProgressDialog(MainPastilla.this);

        String pastilla=getIntent().getStringExtra("pastilla");

        //Colocamos la pastilla
        name_pastilla=(EditText)findViewById(R.id.etNamePastilla);
        name_pastilla.setText(pastilla);
        name_pastilla.setEnabled(false);

        consultaPastilla(pastilla);

        botonModificar=(Button)findViewById(R.id.botonModificar);
        botonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarPastilla(pastilla);
            }
        });

        botonEliminar=(Button)findViewById(R.id.botonEliminar);
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPastilla(pastilla);
            }
        });

    }

    private void consultaPastilla(String pastilla){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Ocultamos el progressDialog
                        try{
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");

                            if(error==true){
                                Toast.makeText(MainPastilla.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                rellenarHorario(mensaje);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainPastilla.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("name_pastilla", pastilla);
                return parametros;
            }
        }   ;

        queue.add(stringRequest);
    }

    private void rellenarHorario(String mensaje){
        String [] horario = mensaje.split("-");

        CheckBox chDes, chAlm, chMer, chCen;

        desAnt=Boolean.parseBoolean(horario[0]);
        almAnt=Boolean.parseBoolean(horario[1]);
        merAnt=Boolean.parseBoolean(horario[2]);
        cenAnt=Boolean.parseBoolean(horario[3]);

        chDes = (CheckBox)findViewById(R.id.chDes);
        chDes.setChecked(desAnt);

        chAlm = (CheckBox)findViewById(R.id.chAlm);
        chAlm.setChecked(almAnt);

        chMer = (CheckBox)findViewById(R.id.chMer);
        chMer.setChecked(merAnt);

        chCen = (CheckBox)findViewById(R.id.chCen);
        chCen.setChecked(cenAnt);
    }

    private void modificarPastilla(String pastilla){

        CheckBox chDes = (CheckBox)findViewById(R.id.chDes);
        String Des=Boolean.toString(chDes.isChecked());

        CheckBox chAlm = (CheckBox)findViewById(R.id.chAlm);
        String Alm=Boolean.toString(chAlm.isChecked());

        CheckBox chMer = (CheckBox)findViewById(R.id.chMer);
        String Mer=Boolean.toString(chMer.isChecked());

        CheckBox chCen = (CheckBox)findViewById(R.id.chCen);
        String Cen=Boolean.toString(chCen.isChecked());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Ocultamos el progressDialog
                        try{
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");

                            if(error==true){
                                Toast.makeText(MainPastilla.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainPastilla.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainPastilla.this, MainPastillero.class);
                                startActivity(i);
                                envioModificarPastilero(Des,Alm,Mer,Cen);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainPastilla.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();

                parametros.put("usuario", usuario);
                parametros.put("password", password);
                parametros.put("name_pastilla", pastilla);
                parametros.put("desayuno", Des);
                parametros.put("almuerzo", Alm);
                parametros.put("merienda", Mer);
                parametros.put("cena", Cen);
                return parametros;
            }
        }   ;

        queue.add(stringRequest);


    }

    private void eliminarPastilla(String pastilla){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Ocultamos el progressDialog
                        try{
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");

                            if(error==true){
                                Toast.makeText(MainPastilla.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainPastilla.this, mensaje, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainPastilla.this, MainPastillero.class);
                                startActivity(i);
                                envioDeletePastillero();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainPastilla.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password", password);
                parametros.put("name_pastilla", pastilla);
                return parametros;
            }
        }   ;

        queue.add(stringRequest);


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

    private void envioDeletePastillero(){
        String topic = "/ESP32/"+usuario+"/DeletePastillas";
        String payload;
        if(desAnt){
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
        if(almAnt){
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
        if(merAnt){
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
        if(cenAnt){
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

    private void envioModificarPastilero(String Des, String Alm, String Mer, String Cen){
        String topicDelete = "/ESP32/"+usuario+"/DeletePastillas";
        String topicAdd = "/ESP32/"+usuario+"/AddPastillas";
        String payload;

        Boolean desNew=Boolean.parseBoolean(Des);
        Boolean almNew=Boolean.parseBoolean(Alm);
        Boolean merNew=Boolean.parseBoolean(Mer);
        Boolean cenNew=Boolean.parseBoolean(Cen);

        //Comparo los anteriores y los nuevos
        if(desNew!=desAnt){
            if(desAnt){                     //Era true y ahora es false, por lo tanto se elimina
                payload="D";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicDelete, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }else if(desNew){
                payload="D";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicAdd, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        if(almNew!=almAnt){
            if(almAnt){                     //Era true y ahora es false, por lo tanto se elimina
                payload="A";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicDelete, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }else if(almNew){
                payload="A";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicAdd, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        if(merNew!=merAnt){
            if(merAnt){                     //Era true y ahora es false, por lo tanto se elimina
                payload="M";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicDelete, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }else if(merNew){
                payload="M";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicAdd, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        if(cenNew!=cenAnt){
            if(cenAnt){                     //Era true y ahora es false, por lo tanto se elimina
                payload="C";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicDelete, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }else if(cenNew){
                payload="C";
                try {
                    byte[] encodedPayload = new byte[0];
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topicAdd, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}