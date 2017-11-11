package br.org.cesar.smartlock.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.org.cesar.smartlock.MainActivity;

/**
 * Created by Tiago on 09/11/2017.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        Utils.verifyStatusDoorLock(context);
        Utils.scheduleNotification(context, 100000, 0);
    }
}
