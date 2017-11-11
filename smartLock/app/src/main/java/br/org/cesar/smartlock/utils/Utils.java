package br.org.cesar.smartlock.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import br.org.cesar.smartlock.MainActivity;
import br.org.cesar.smartlock.R;
import br.org.cesar.smartlock.interfaces.IAmarinoCommand;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Tiago on 29/10/2017.
 */

public class Utils {

    public static final int sNOTIFICATION_ID = 1000;
    public static boolean isUserLogged = false;

    public static ProgressDialog createSimpleProgressDialog(String title, String message, Context context) {
        return ProgressDialog.show(context, title, message, true);
    }

    public static String getMac(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    public static void createSimpleNotification(Context context, String title, String content, Class activityClass) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_lock)
                .setContentTitle(title)
                .setContentText(content);
        Intent resultIntent = new Intent(context, activityClass);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(sNOTIFICATION_ID, builder.build());
    }

    public static void scheduleNotification(Context context, long delay, int notificationId) {

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static void verifyStatusDoorLock(final Context context){

        if(!AmarinoUtil.IsConnected) return;
        AmarinoUtil.getDataToArduino(new IAmarinoCommand(){
            @Override
            public void callback(String dataReturned) {
                boolean isLocked = dataReturned.equals("T") ? true : false;

                if(isLocked){
                    createSimpleNotification(context, "Smart Lock", "Your door is not locked", MainActivity.class);
                }

            }
        }, context, AmarinoUtil.GetStatusDoorLockFlag);
    }
}
