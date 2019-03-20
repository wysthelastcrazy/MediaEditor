package com.wys.lib;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by yas on 2019/3/19
 * Describe:
 */
public class Constants {
    /**
     * 屏幕宽高
     */
    public static int screenWidth;
    public static int screenHeight;
    public static Context mContext;

    public static void init(Context context) {
        DisplayMetrics mDisplayMetrics = context.getResources()
                .getDisplayMetrics();
        screenWidth = mDisplayMetrics.widthPixels;
        screenHeight = mDisplayMetrics.heightPixels;
        mContext=context;
    }
}
