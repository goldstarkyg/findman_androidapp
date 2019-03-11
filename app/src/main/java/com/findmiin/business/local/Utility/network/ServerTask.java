package com.findmiin.business.local.Utility.network;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;


import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.Utility.util.WebHttpUtils;
import com.findmiin.business.local.manager.utils.CheckUtils;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

//import llm.oneping.network.ServerManager;

@SuppressLint("NewApi")
public class ServerTask extends AsyncTask<String, String, String> {

//    public static final String SERVER_URL ="http://192.168.1.240:9000/api/";
//    public static final String SERVER_IMAGE ="http://192.168.1.240:9000/";

    public static final String SERVER_URL ="http://findmiin.com/api/";
    public static final String SERVER_IMAGE ="http://findmiin.com";

    public static final String SERVER_FILE_URL = "http://192.168.1.251:9000/uploads/";
    public static final String SERVER_IMAGE_URL = "http://192.169.197.230/~xcoktapp/uploads/users/";

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String COOKIE_KEY = "com.sin.contact2w.cookie";
    public static final String SERVER_UPLOAD_PHOTO_PATH = SERVER_URL + "addsosophoto";
    public static final String OFFLINE_REQUEST_KEY = "offline_request_key";
    public final static String SECURITY_KEY = "token";//123
    public final static int OFFLINE_MODE = 0;
    public final static int ONLINE_MODE = 1;
    public static int g_nConnectionMode = ONLINE_MODE;
/////////////////////////////////////////////////////////////////////////////////////////////////
    private String mCommand;
    private HashMap<String, String> mParam;

    private List<String> mCommandList;
    private List<HashMap<String, String>> mParamList = null;

    private ResultCallBack mCallBack;

    private int m_nRequestMode = 0;        // 0: once mode, 1: all once mode

    public ServerTask(String command, ResultCallBack callback,
                      HashMap<String, String> param) {
        this.mCommand = command;
        this.mCallBack = callback;
        this.mParam = param;

        m_nRequestMode = 0;
    }

    public ServerTask(List<String> commandList, ResultCallBack callback,
                      List<HashMap<String, String>> paramList) {
        this.mCommandList = commandList;
        this.mCallBack = callback;
        this.mParamList = paramList;

        m_nRequestMode = 1;
    }

    @Override
    protected String doInBackground(String... params) {
        if (m_nRequestMode == 0)
            return request(mCommand, mParam);
        else {
            String result = "";
            for (int i = 0; i < mParamList.size(); i++) {
                result = request(mCommandList.get(i), mParamList.get(i));
                getRequestResult(result);
            }

            return result;
        }
    }

    private String request(String command, HashMap<String, String> param) {
        boolean isOfflineEnabled = false;
        HashMap<String, String> hashMap = new HashMap<String, String>();
//		hashMap.put("action", command );

        // add security key
        String security_key = DataUtils.getPreference(SECURITY_KEY, "");
        if (CheckUtils.isEmpty(security_key) == false)
            hashMap.put(SECURITY_KEY, security_key);

        if (param != null) {
            param.keySet();
            for (String key : param.keySet()) {
                String pKey = key;
                if (key.equals(OFFLINE_REQUEST_KEY)) {
                    isOfflineEnabled = true;
                    continue;
                }

                hashMap.put(pKey, param.get(key));
                Log.d("LOGCAT", pKey + ":" + param.get(key));
            }
        }

//		if( (g_nConnectionMode == OFFLINE_MODE && command.equals("login") == false && command.equals("register")) ||
//				NetworkUtils.isInternateAvaliable() == false)
//		{
//			if( isOfflineEnabled == true )
//				return loadHttpResponseResult(hashMap, command);
//			else
//				return "";
//		}


        String cookie = DataUtils.getPreference(COOKIE_KEY, "");

        String[] results = WebHttpUtils.postDataWithCookie(
                SERVER_URL + command, hashMap, cookie);

        if (results == null)        // not receive Http response
        {
            if (isOfflineEnabled == true)
                return loadHttpResponseResult(hashMap, command);
            else
                return "";
        } else {
            if (CheckUtils.isEmpty(results[1]) == false) {
                if (command.equals("LOGOUT"))
                    DataUtils.savePreference(COOKIE_KEY, "");
                else
                    DataUtils.savePreference(COOKIE_KEY, results[1]);
            }

            if (isOfflineEnabled == true && CheckUtils.isEmpty(results[0]) == false)
                saveHttpResponseResult(hashMap, command, results[0]);
        }
        return results[0];
    }

    private void getRequestResult(String result) {
        LogicResult serverResult = new LogicResult();
        serverResult.mContent = "" + result;
        if (result == null) {
            serverResult.mResult = LogicResult.RESULT_FAIL;
            serverResult.mMessage = "Server is not connected";
            Log.e("marsor", serverResult.mMessage);
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }

        int idx = 0;
//		if((idx = result.indexOf("\\")) != -1){
//			result = result.substring(0,idx)+ result.substring(idx+1,result.length());
//		}
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("marsor", e.getMessage(), e);
            serverResult.mResult = LogicResult.RESULT_FAIL;
            ;
            serverResult.mMessage = "Server is not responding";
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }
        if (json != null) {
            try {
                serverResult.mResult = json.getInt("status_code");
//                serverResult.mMessage = json.getString("error_msg");
                serverResult.setData(json);

                if (mCommand.equals("login") || mCommand.equals("register")) {
                    saveSecurityKey(serverResult.getData().optJSONObject("content"));
                }
            } catch (JSONException e) {
                Log.e("marsor", e.getMessage(), e);
                serverResult.mResult = LogicResult.RESULT_FAIL;
                serverResult.mMessage = "Server is not responding";
                if (mCallBack != null) mCallBack.doAction(serverResult);
                return;
            }

        }

        if (mCallBack != null) {
            if (mCallBack != null) mCallBack.doAction(serverResult);
        }

        super.onPostExecute(result);
    }

    @Override
    protected void onPostExecute(String result) {
        if (m_nRequestMode != 0)
            return;

        getRequestResult(result);

        super.onPostExecute(result);
    }

    // : login  username=aaa pwd=www then  : login username aaa pwd www : the response server sent.
    private static void saveHttpResponseResult(HashMap<String, String> requestMap, String action, String response) {
        if (response.isEmpty())
            return;

        String key = "" + action;
        if (requestMap != null && requestMap.size() != 0) {
            for (String strName : requestMap.keySet()) {
                if (strName.equals(SECURITY_KEY))
                    continue;
                key += (strName + requestMap.get(strName));
            }
        }
        if (key.isEmpty())
            return;

        DataUtils.savePreference(key, response);
    }

    private static String loadHttpResponseResult(HashMap<String, String> requestMap, String action) {
        String response = "";

        String key = "" + action;
        if (requestMap != null && requestMap.size() != 0) {
            for (String strName : requestMap.keySet()) {
                if (strName.equals(SECURITY_KEY))
                    continue;
                key += (strName + requestMap.get(strName));
            }
        }

        if (key.isEmpty())
            return response;

        response = DataUtils.getPreference(key, "");

        return response;
    }

    public static void saveSecurityKey(JSONObject data) {
        if (data == null)
            return;

        String security_key = data.optString(SECURITY_KEY, "");
        if (CheckUtils.isEmpty(security_key))
            return;

        DataUtils.savePreference(SECURITY_KEY, security_key);
    }

    public static void removeSecurityKey() {
        DataUtils.savePreference(SECURITY_KEY, "");
    }

    public static void setOfflineMode() {
        g_nConnectionMode = OFFLINE_MODE;
    }

    public static void setOnlineMode() {
        g_nConnectionMode = ONLINE_MODE;
    }

    public static int getConnectionMode() {
        return g_nConnectionMode;
    }
}

