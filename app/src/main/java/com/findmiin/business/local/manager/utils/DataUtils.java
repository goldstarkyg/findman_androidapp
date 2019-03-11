package com.findmiin.business.local.manager.utils;import android.app.Activity;import android.content.Context;import android.content.SharedPreferences;import android.content.pm.ApplicationInfo;import android.content.pm.PackageManager;import android.util.Log;public class DataUtils {	private static Context m_Context = null;    private static SharedPreferences preferences = null;    public static void setContext(Context context)    {    	m_Context = context;    }    private static SharedPreferences getPreferences()    {        if(preferences == null)        {            if(m_Context == null)                return null;            preferences = m_Context.getSharedPreferences("Preferences", 0);        }        return preferences;    }    public static void savePreference(String key, Boolean value)    {        try        {            getPreferences().edit().putBoolean(key, value.booleanValue()).commit();        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u4FDD\u5B58\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }    }    public static boolean getPreference(String key, boolean defValue)    {        try        {            return getPreferences().getBoolean(key, defValue);        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u83B7\u53D6\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }        return defValue;    }    public static void savePreference(String key, float value)    {        try        {            getPreferences().edit().putFloat(key, value).commit();        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u4FDD\u5B58\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }    }    public static float getPreference(String key, float defValue)    {        try        {            return getPreferences().getFloat(key, defValue);        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u83B7\u53D6\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }        return defValue;    }    public static void savePreference(String key, int value)    {        try        {            getPreferences().edit().putInt(key, value).commit();        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u4FDD\u5B58\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }    }    public static int getPreference(String key, int defValue)    {        try        {            return getPreferences().getInt(key, defValue);        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u83B7\u53D6\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }        return defValue;    }    public static void savePreference(String key, long value)    {        try        {            getPreferences().edit().putLong(key, value).commit();        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u4FDD\u5B58\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }    }    public static long getPreference(String key, long defValue)    {        try        {            return getPreferences().getLong(key, defValue);        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u83B7\u53D6\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }        return defValue;    }    public static void savePreference(String key, String value)    {        try        {            getPreferences().edit().putString(key, value).commit();        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u4FDD\u5B58\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }    }    public static String getPreference(String key, String defValue)    {        try        {            return getPreferences().getString(key, defValue);        }        catch(Exception e)        {            Log.e("AndroidCommon", "\u83B7\u53D6\u504F\u597D\u8BBE\u7F6E\u65F6\u51FA\u9519\uFF01", e);        }        return defValue;    }     public static String getApplicationMeta(Activity act, String metaKey)    {        String result = "";        if(act == null)            return result;        try        {            PackageManager manager = act.getPackageManager();            if(manager != null)            {                ApplicationInfo appInfo = manager.getApplicationInfo(act.getPackageName(), 128);                if(appInfo != null)                    result = appInfo.metaData.getString(metaKey);            }        }        catch(Exception e) { }        return result;    }}