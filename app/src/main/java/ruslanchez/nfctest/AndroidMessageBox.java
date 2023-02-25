package ruslanchez.nfctest;

import android.app.Activity;
import android.app.AlertDialog;

public class AndroidMessageBox {
    public static void showMessageBox(Activity activity, String title, String message)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle(title)
                .setMessage(message);
        alertBuilder.create().show();
    }

    public static void showMessagebox(Activity activity, String message)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setMessage(message);
        alertBuilder.create().show();
    }
}

