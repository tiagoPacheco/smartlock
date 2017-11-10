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
import br.org.cesar.smartlock.MainActivity;
import br.org.cesar.smartlock.interfaces.IAmarinoCommand;

/**
 * Created by Tiago on 29/10/2017.
 */

public class AmarinoUtil {

    //Flags
    public static final char LoginFlag = 'A';
    public static final char SignUpFlag = 'B';
    public static final char GetStatusDoorLockFlag = 'C';
    public static final char DoorLockFlag = 'D';

    //Methods
    public static final String CommandLock = "Lock";
    public static final String CommandUnlock = "Unlock";
    public static final String SignUp = "SignUp";
    public static final String SignIn = "SignIn";
    public static boolean IsConnected = false;

    public static final String Success = "Success";
    public static final String InvalidMac = "InvalidMac";
    public static final String InvalidUserOrPassword = "InvalidUserOrPassword";
    public static final String DoorOpened = "DoorOpened";

    public static final String Address = "20:13:01:24:14:05";
    public static String DataReturned = null;

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

                    if(DataReturned.equals(DoorOpened)){
                        Class activityClass = Utils.isUserLogged ? MainActivity.class : LoginActivity.class;
                        Utils.createSimpleNotification(context, "Smart Lock", "The door was opened", activityClass);
                    }
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

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return AmarinoUtil.DataReturned;
            }

            @Override
            protected void onPostExecute(String dataReturned) {
                mProgressDialog.dismiss();
                amarinoCommand.callback(dataReturned);
            }
        }.execute();
    }

    public static void getDataToArduino(final IAmarinoCommand amarinoCommand,
                                       final Context context, final char flagMethod) {

        new AsyncTask<Object, Object, String>() {
            @Override
            protected void onPreExecute() {
                mProgressDialog = Utils.createSimpleProgressDialog("Wait", "Processing...", context);
            }

            @Override
            protected String doInBackground(Object... params) {

                Amarino.sendDataToArduino(context, Address, flagMethod, ' ');

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return AmarinoUtil.DataReturned;
            }

            @Override
            protected void onPostExecute(String dataReturned) {
                mProgressDialog.dismiss();
                amarinoCommand.callback(dataReturned);
            }
        }.execute();
    }
}
