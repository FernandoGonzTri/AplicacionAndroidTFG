package com.example.pastillerointeligente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.companion.BluetoothDeviceFilter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainComunicacion extends AppCompatActivity {

    Button btnEnviar;
    EditText et1, et2;

    String usuario, info, password;
    TextView tvuser;

    Handler bluetoohIn;
    final int handlerState = 0;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private StringBuilder DataString = new StringBuilder();
    private ConnectedThread MyConexionBT;
    BluetoothSocket btSocket;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-000-1000-8000-00805F9B34FB");

    private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_comunicacion);

        //Si se recibe un mensaje
        bluetoohIn = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
                if(msg.what == handlerState){

                }
            }
        };

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        verificarEstadoBluetooh();

        btnEnviar = findViewById(R.id.button3);
        et1 = findViewById(R.id.editTextWiFi);
        et2 = findViewById(R.id.editTextWifiPas);




        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datos = et1.getText().toString();
                MyConexionBT.write(datos);
                datos=et2.getText().toString();
                MyConexionBT.write(datos);
                MyConexionBT.write(usuario);
                MyConexionBT.write(password);

                SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Pastillero", "");
                editor.commit();
                finish();

                Intent i = new Intent(MainComunicacion.this, MainPastillero.class);
                startActivity(i);
            }
        });
    }

    private BluetoothSocket createBluetoohSocket(BluetoothDevice device) throws IOException{
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void onResume(){
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra("device-address");
        usuario = intent.getStringExtra("Usuario");


        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        password=preferences.getString("Contraseña","");

        Toast.makeText(MainComunicacion.this, info, Toast.LENGTH_LONG).show();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        try{
            btSocket = createBluetoohSocket(device);
        } catch (IOException e) {
            Toast.makeText(MainComunicacion.this, "La creacion del socket fallo", Toast.LENGTH_LONG).show();
        }

        try{
            btSocket.connect();
        } catch (IOException e) {
            try{
                btSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    public void onPause(){
        super.onPause();
        try{
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verificarEstadoBluetooh(){
        bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Toast.makeText(MainComunicacion.this, "El dispositivo no soporta Bluetooh", Toast.LENGTH_LONG).show();
        }else{
            if (!bluetoothAdapter.isEnabled()) {
                //Se solicita que se active el blueetoh
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }else{
                Log.d("MainComunicacion","---Bluetooh Activado---");
            }
        }
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("MainComunicacion", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("MainComunicacion", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte [] mmBuffer = new byte[1];


            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    char ch = (char) mmBuffer[0];
                    bluetoohIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    Log.d("MainComunicacion", "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
                input = "\n";
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}