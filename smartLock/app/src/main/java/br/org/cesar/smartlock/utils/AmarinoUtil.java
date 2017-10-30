package br.org.cesar.smartlock.utils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.widget.Toast;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

/**
 * Created by Tiago on 29/10/2017.
 */

public class AmarinoUtil {

    public static final char LoginFlag = 'A';
    public static final String InvalidName = "InvalidName";
    public static final String InvalidPassword = "InvalidPassword";
    public static final String InvalidUserAndPassword = "InvalidUserAndPassword";
    public static final String Success = "Success";

    public static boolean IsConnected = false;

    public static final String Address = "20:13:01:24:14:05";
    public static String DataReturned = null;
    public static boolean IsDataReceived = false;

    public static void registerConnectionReceiver(final Context contextIntent){
        IntentFilter actionsFilter = new IntentFilter();
        actionsFilter.addAction(AmarinoIntent.ACTION_CONNECTED);
        actionsFilter.addAction(AmarinoIntent.ACTION_DISCONNECTED);
        actionsFilter.addAction(AmarinoIntent.ACTION_RECEIVED);

        contextIntent.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(AmarinoIntent.ACTION_CONNECTED.equals(intent.getAction())){
                    Toast.makeText(contextIntent, "Bluetooth connected", Toast.LENGTH_SHORT).show();
                    IsConnected = true;
                }
                else if(AmarinoIntent.ACTION_DISCONNECTED.equals(intent.getAction())){
                    Toast.makeText(contextIntent, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
                    IsConnected = false;
                }
                else if(AmarinoIntent.ACTION_RECEIVED.equals(intent.getAction())){
                    DataReturned = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
                    IsDataReceived = true;
                }
            }
        }, actionsFilter);
    }

    public static void connect(final Context contextIntent){
        Amarino.connect(contextIntent, Address);
    }

    public static void disconnect(final Context contextIntent){
        Amarino.disconnect(contextIntent, Address);
    }

    public static void sendData(Context context, char flag, String data){
        Amarino.sendDataToArduino(context, Address, flag, data);
    }
}
