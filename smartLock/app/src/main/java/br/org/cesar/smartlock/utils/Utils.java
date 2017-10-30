package br.org.cesar.smartlock.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Tiago on 29/10/2017.
 */

public class Utils {

    public static ProgressDialog createSimpleProgressDialog(String title, String message, Context context) {
        return ProgressDialog.show(context, title, message, true);
    }

}
