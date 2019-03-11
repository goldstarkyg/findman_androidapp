package com.findmiin.business.local.manager.activity.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import com.findmiin.business.local.manager.utils.CheckUtils;

import java.util.Stack;

/**
 * Created by JonIC on 2017-01-24.
 */
public class MYActivityManager {
    private static Stack<Activity> activityStack;
    private static MYActivityManager instance;

    public static MYActivityManager getInstance() {
        if (instance == null) {
            instance = new MYActivityManager();
        }
        return instance;
    }

    public Activity currentActivity() {
        if (activityStack != null && !activityStack.isEmpty()) {
            Activity activity = activityStack.lastElement();
            return activity;
        } else {
            return null;
        }
    }

    public boolean isCurrentActivity(final String className) {
        if (CheckUtils.isNotEmpty(className) && activityStack != null && !activityStack.isEmpty()) {
            Activity activity = activityStack.lastElement();
            if (activity != null && activity.getClass().getName().equals(className)) {
                return true;
            }
            activity = null;
        }
        return false;
    }

    public void pushActivity(final Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void popActivity() {
        if (activityStack != null && !activityStack.isEmpty()) {
            Activity activity = activityStack.lastElement();
            if (activity != null) {
                activity.finish();
                activityStack.remove(activity);
            }
            activity = null;
        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            if (activityStack != null){
                activityStack.remove(activity);
            }
        }
        activity = null;
    }

    public void popActivity(String className) {
        if (CheckUtils.isNotEmpty(className) && activityStack != null && !activityStack.isEmpty()) {
            for (Activity activity : activityStack) {
                if (activity != null && activity.getClass().getName().equals(className)) {
                    popActivity(activity);
                    break;
                }
            }
        }
        className = null;
    }

    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }

    public void popAllActivityExceptOne(final Class<?> clazz) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(clazz)) {
                break;
            }
            popActivity(activity);
        }
    }

    public static void changeActivity(Activity srcAct, Class<?> destAct, int requestCode[])
    {
        Intent intent = new Intent();
        intent.setClass(srcAct, destAct);
        if(requestCode == null || requestCode.length == 0)
            srcAct.startActivity(intent);
        else
            srcAct.startActivityForResult(intent, requestCode[0]);
    }

    public static void changeActivity(Activity srcAct, Class<?> destAct, Bundle params, int requestCode[])
    {
        Intent intent = new Intent();
        intent.setClass(srcAct, destAct);
        if(params != null)
            intent.putExtras(params);
        if(requestCode == null || requestCode.length == 0)
            srcAct.startActivity(intent);
        else
            srcAct.startActivityForResult(intent, requestCode[0]);
    }

    public static void changeActivity(Activity srcAct, Class<?> destAct, Bundle params, boolean killOlder, int requestCode[])
    {
        Intent intent = new Intent();
        intent.setClass(srcAct, destAct);
        if(params != null)
            intent.putExtras(params);

        if(requestCode == null || requestCode.length == 0)
        {

            srcAct.startActivity(intent);
            if(killOlder)
                getInstance().popActivity(srcAct);
        } else
        {
            getInstance().pushActivity(srcAct);
            srcAct.startActivityForResult(intent, requestCode[0]);
        }
    }

    public static void changeActivity(Activity srcAct, Class<?> destAct, Bundle params, boolean killOlder, int requestCode )
    {
        Intent intent = new Intent();
        intent.setClass(srcAct, destAct);
        if(params != null)
            intent.putExtras(params);

        if( requestCode < 0 )
        {
            srcAct.startActivity(intent);
            if(killOlder)
                getInstance().popActivity(srcAct);
        }
        else
        {
            srcAct.startActivityForResult(intent, requestCode);
        }
    }

    public static void changeActivity(Activity srcAct, Class<?> destAct, boolean killOlder, int requestCode[])
    {
        Intent intent = new Intent();
        intent.setClass(srcAct, destAct);
        if(requestCode == null || requestCode.length == 0)
        {
            srcAct.startActivity(intent);
            if(killOlder)
                getInstance().popActivity(srcAct);
        } else
        {
            srcAct.startActivityForResult(intent, requestCode[0]);
        }
    }

    public static void changeActivity(Activity srcAct, Class<Activity> destAct, boolean killOlder, Bundle params, int requestCode[])
    {
        Intent intent = new Intent();
        intent.setClass(srcAct, destAct);
        if(params != null)
            intent.putExtras(params);
        if(requestCode == null || requestCode.length == 0)
        {
            srcAct.startActivity(intent);
            if(killOlder)
                getInstance().popActivity(srcAct);
        } else
        {
            srcAct.startActivityForResult(intent, requestCode[0]);
        }
    }
}
