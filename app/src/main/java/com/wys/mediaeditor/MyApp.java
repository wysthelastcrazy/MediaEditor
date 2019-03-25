package com.wys.mediaeditor;

import android.app.Application;
import android.content.Context;

import com.wys.lib.Constants;

/**
 * Created by yas on 2019/3/20
 * Describe:
 */
public class MyApp extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init(this);
        mContext=this;
    }

    public static Context getContext() {
        return mContext;
    }
}
