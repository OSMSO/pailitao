package com.himi.app_pailitao;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by liuchaoya on 2018/6/28.
 */

public class App extends Application {

    public static App THIS;

    public static String cookiestr;

    @Override
    public void onCreate() {
        super.onCreate();

        THIS = this;

        FileUtils.init();
        DisplayUtils.init();
        CacheUtils.init();

        Fresco.initialize(this);
    }
}
