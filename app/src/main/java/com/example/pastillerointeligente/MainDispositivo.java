package com.example.pastillerointeligente;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainDispositivo extends AppCompatActivity {

    private static final String TAG = "MainDispositivo";
    String usuario;

    ListView IdLista;

    public static String EXTRA_DEVICE_ADDRESS = "device-address";

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dispositivo);
        usuario = getIntent().getStringExtra("Usuario");
    }

    public void onResume(){
        super.onResume();
        //Comprobar conexion Bluetooh
        verificarEstadoBluetooh();

        //Inicializar array que contiene los dispoisitivos vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.dispositivos_encontrados);
        //Se presenta en el listview
        IdLista = (ListView)findViewById(R.id.IdLista);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size()>0){
            for(BluetoothDevice device : pairedDevices){
                mPairedDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }


    }

    //Se configura el click de la lista
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView av, View v, int arg, long arg2) {

            //Se obtiene la mac que son los ultimos 17 caracteres.
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length()-17);

            finishAffinity();

            //Mandamos la direccion a la otra activity
            Intent intend = new Intent(MainDispositivo.this, MainComunicacion.class);
            intend.putExtra(EXTRA_DEVICE_ADDRESS, address);
            intend.putExtra("Usuario", usuario);

            startActivity(intend);

        }
    };

    private void verificarEstadoBluetooh(){
        bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Toast.makeText(MainDispositivo.this, "El dispositivo no soporta Bluetooh", Toast.LENGTH_LONG).show();
        }else{
            if (!bluetoothAdapter.isEnabled()) {
                //Se solicita que se active el blueetoh
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }else{
                Log.d(TAG,"---Bluetooh Activado---");
            }
        }
    }
}