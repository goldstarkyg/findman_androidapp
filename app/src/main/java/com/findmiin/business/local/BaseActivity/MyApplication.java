package com.findmiin.business.local.BaseActivity;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.design.ScreenAdapter;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.findmiin.business.local.manager.utils.MessageUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by JonIC on 2017-01-24.
 */
public class MyApplication extends MultiDexApplication {

    public void onCreate() {
        super.onCreate();
        initScreenAdapter();
        setContextToComponents();
        InitConst();

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("MyApplication","success firebase initialize!");
    }
    private void setContextToComponents() {
        DataUtils.setContext(this);
        MessageUtils.setApplicationContext(this);
    }
    @Override
    protected  void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void initScreenAdapter(){
        ScreenAdapter.setApplicationContext(this);
        ScreenAdapter.setDefaultSize(720, 1280);
    }
    protected void InitConst(){
        int speech_size = LayoutUtils.getHeight(30);
        DataUtils.savePreference(Const.TEXT_SIZE_1,speech_size);
        int phoneFontSize = LayoutUtils.getHeight(60);
        DataUtils.savePreference(Const.PHONE_FONT_SIZE,phoneFontSize);

        int progress_pos_y = ScreenAdapter.computeHeight(250);
        DataUtils.savePreference(Const.PROGRESS_POS_Y, progress_pos_y);

        DataUtils.savePreference(Const.TIMEOUT_CONNECTION, 20000);
        DataUtils.savePreference(Const.TIMEOUT_SOCKET, 20000);
    }

}
