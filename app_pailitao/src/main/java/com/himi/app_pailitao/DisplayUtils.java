
package com.himi.app_pailitao;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.lang.reflect.Field;

public class DisplayUtils {

    public static float density;
    public static int densityDpi;
    public static int screenWidth;
    public static int screenHeight;

    public static void init() {
        DisplayMetrics metrics = App.THIS.getResources().getDisplayMetrics();
        density = metrics.density;
        densityDpi = metrics.densityDpi;
        if (metrics.widthPixels > metrics.heightPixels) {
            screenWidth = metrics.heightPixels;
            screenHeight = metrics.widthPixels;
        } else {
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }
    }

    public static int getDp2Px(int dp) {
        return (int) (dp * density + .5f);
    }

    public static int getDp2Px(float dp) {
        return (int) (dp * density + .5f);
    }

    public static String getDensityDpi() {
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";

            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";

            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";

            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";

            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";

            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";

            default:
                return "mdpi";
        }
    }

    // 获取手机状态栏高度
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = App.THIS.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (statusBarHeight == 0) {
            if (screenWidth <= 720) {
                statusBarHeight = 48;
            } else if (screenWidth > 720 && screenWidth <= 1080) {
                statusBarHeight = 72;
            } else {
                statusBarHeight = 96;
            }
        }
        return statusBarHeight;
    }

    // 获取ActionBar的高度
    public static int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (App.THIS.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))// 如果资源是存在的、有效的
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, App.THIS.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

}
