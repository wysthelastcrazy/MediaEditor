package com.wys.mediaeditor;

import android.app.Application;

import com.wys.lib.Constants;

/**
 * Created by yas on 2019/3/20
 * Describe:
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init(this);
    }
}
