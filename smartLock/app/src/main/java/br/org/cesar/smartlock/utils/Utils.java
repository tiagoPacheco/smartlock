package br.org.cesar.smartlock.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import br.org.cesar.smartlock.R;

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
}
