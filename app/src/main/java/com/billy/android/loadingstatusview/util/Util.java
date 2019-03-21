package com.billy.android.loadingstatusview.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Locale;

/**
 * @author billy.qi
 * @since 19/3/19 22:52
 */
public class Util {

    /**
     * check if the network connected or not
     * @param context context
     * @return true: connected, false:not, null:unknown
     */
    public static Boolean isNetworkConnected(Context context) {
        try {
            context = context.getApplicationContext();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getErrorImage() {
        return "http://www." + System.currentTimeMillis() + ".com/abc.png";
    }

    public static String getRandomImage() {
        int id = (int) (Math.random() * 100000);
        return String.format(Locale.CHINA, "https://www.thiswaifudoesnotexist.net/example-%d.jpg", id);
    }
}
