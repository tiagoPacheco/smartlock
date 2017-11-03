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
import br.org.cesar.smartlock.LoginActivity;
import br.org.cesar.smartlock.interfaces.IAmarinoCommand;

/**
 * Created by Tiago on 29/10/2017.
 */

public class AmarinoUtil {

    public static final char LoginFlag = 'A';
    public static final char SignUpFlag = 'B';
    public static final String InvalidMac = "InvalidMac";
    public static final String InvalidUserOrPassword = "InvalidUserOrPassword";
    public static final String SignUp = "SignUp";
    public static final String SignIn = "SignIn";
    public static final String Success = "Success";

    public static boolean IsConnected = false;

    public static final String Address = "20:13:01:24:14:05";
    public static String DataReturned = null;
    public static boolean IsDataReceived = false;

    private static ProgressDialog mProgressDialog = null;

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

    public static void sendDataToArduinoWithReturn(final String[] data, final IAmarinoCommand amarinoCommand,
                                            final Context context, final char flagMethod) {

        new AsyncTask<Object, Object, String>() {
            @Override
            protected void onPreExecute() {
                mProgressDialog = Utils.createSimpleProgressDialog("Wait", "Processing...", context);
            }

            @Override
            protected String doInBackground(Object... params) {

                Amarino.sendDataToArduino(context, Address, flagMethod, data);

                while (!AmarinoUtil.IsDataReceived);

                return AmarinoUtil.DataReturned;
            }

            @Override
            protected void onPostExecute(String dataReturned) {
                AmarinoUtil.IsDataReceived = false;
                mProgressDialog.dismiss();
                amarinoCommand.callback(dataReturned);
            }
        }.execute();
    }
}
