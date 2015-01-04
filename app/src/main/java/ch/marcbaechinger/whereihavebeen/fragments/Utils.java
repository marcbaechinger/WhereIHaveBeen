package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import app.ch.marcbaechinger.whereihavebeen.R;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    private static final java.lang.String JPEG_SUFFIX = ".jpg";

    /*
         * Making sure public utility methods remain static
         */
    private Utils() {
    }

    /**
     * Returns the screen/display size
     *
     * @param context
     * @return
     */
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new Point(width, height);
    }

    /**
     * Shows an error dialog with a given text message.
     *
     * @param context
     * @param errorString
     */

    public static final void showErrorDialog(Context context, String errorString) {
        new AlertDialog.Builder(context).setTitle(R.string.error)
                .setMessage(errorString)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     *
     * @param millis
     * @return
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }

    public static int dpToPx(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public static int pxToDp(int px, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) px / density);
    }
}
