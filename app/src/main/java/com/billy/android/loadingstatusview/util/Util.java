package com.billy.android.loadingstatusview.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import java.util.Locale;

/**
 * @author billy.qi
 * @since 19/3/19 22:52
 */
public class Util {

    /**
     * check if the network connected or not
     *
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

    //旋转动画
    public static void startRotateAnimation(View view) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        view.startAnimation(rotateAnimation);
    }
}
