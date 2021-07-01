package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainConsultaPastillas extends AppCompatActivity {

    RequestQueue queue;
    ProgressDialog progressDialog;
    String url = "http://pastilleroesp32.ddns.net/ESP32/consultapastillas.php";

    String usuario,password;

    ListView lv1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_consulta_pastillas);

        lv1=(ListView)findViewById(R.id.lvconsulta);

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario=preferences.getString("Usuario","");
        password=preferences.getString("Contraseña","");

        //Se inicializa el queue
        queue = Volley.newRequestQueue(MainConsultaPastillas.this);
        progressDialog = new ProgressDialog(MainConsultaPastillas.this);

        consultaPastillas();
    }

    private void consultaPastillas(){


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
                                Toast.makeText(MainConsultaPastillas.this, mensaje, Toast.LENGTH_LONG).show();
                            }else{
                                recibirPastillas(mensaje);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainConsultaPastillas.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password",password);
                return parametros;
            }
        }   ;

        queue.add(stringRequest);

    }

    private void recibirPastillas(String mensaje){
        String [] pastillas = mensaje.split("/");

        ArrayList<String> lista = new ArrayList<>();
        ArrayAdapter adapter;

        int cont=1;
        int tam=pastillas.length;
        while(tam>cont){
            lista.add(pastillas[cont]);
            cont++;
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lista);
        lv1.setAdapter(adapter);
        lv1.setOnItemClickListener(pulsacionPastilla);
    }

    //Se configura el click de la lista
    private AdapterView.OnItemClickListener pulsacionPastilla = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView av, View v, int arg, long arg2) {

            String info = ((TextView) v).getText().toString();

            //Mandamos la direccion a la otra activity
            Intent intend = new Intent(MainConsultaPastillas.this, MainPastilla.class);
            intend.putExtra("pastilla", info);

            startActivity(intend);

        }
    };
}