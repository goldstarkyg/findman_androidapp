package com.findmiin.business.local.Activity;


import android.Manifest;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.findmiin.business.local.Activity.Login.LoginActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.design.ScreenAdapter;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

/**
 * Created by JonIC on 2016-11-14.
 */
public class SplashActivity extends Activity {


    ImageView m_imgSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.red));
        findView();
        startAlphaAnimation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SplashActivity.this,"OK. get record permission", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    return;
                } else {
                    Toast.makeText(SplashActivity.this,"Fail to get permission", Toast.LENGTH_SHORT).show();
                    return;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void findView() {
        m_imgSplash = (ImageView) findViewById(R.id.img_splash);
        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;

        int height =(int)(width * 0.225);
        LayoutUtils.setSize(m_imgSplash, width, height, false );
    }

    private void startAlphaAnimation() {

        m_imgSplash.setVisibility(View.VISIBLE);
        Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.spl_animation1);
        if (m_imgSplash != null) {
            m_imgSplash.startAnimation(hyperspaceJump);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onFinishAnimation();
            }
        }, 1500);
    }

    protected void onFinishAnimation() {

        gotoMainPage();
    }

    private void gotoMainPage() {
        String loginState = DataUtils.getPreference(Const.LOGIN_STATE, Const.LOGOUT);
        if(loginState.equals(Const.LOGOUT)){
            Bundle bundle = new Bundle();
            MYActivityManager.changeActivity(this, LoginActivity.class, bundle, true, null);
            overridePendingTransition(R.anim.anim_fade_out, R.anim.anim_fade_in);
        }
        if(loginState.equals(Const.LOGIN)){
            Bundle bundle = new Bundle();
            MYActivityManager.changeActivity(this, MainActivity.class, bundle, true, null);
            overridePendingTransition(R.anim.anim_fade_out, R.anim.anim_fade_in);
        }
    }


}
