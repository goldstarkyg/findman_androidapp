package com.findmiin.business.local.Utility.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;


import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.NetworkUtils;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.Utility.util.WebHttpUtils;
import com.findmiin.business.local.manager.utils.CheckUtils;
import com.findmiin.business.local.manager.utils.DataUtils;

import java.util.HashMap;
import java.util.List;
//import llm.oneping.network.ServerManager;

@SuppressLint("NewApi")
public class WebServiceTask extends AsyncTask<String, String, String> {
    public static final String OFFLINE_REQUEST_KEY = "offline_request_key";

    private String m_ServerURL = "";
    private HashMap<String, String> mParam;

    private List<String> mCommandList;
    private List<HashMap<String, String>> mParamList = null;

    private ResultCallBack mCallBack;

    private int m_nRequestMode = 0;        // 0: once mode, 1: all once mode

    public WebServiceTask(String url, ResultCallBack callback,
                          HashMap<String, String> param) {
        this.m_ServerURL = url + "";
        this.mCallBack = callback;
        this.mParam = param;

        m_nRequestMode = 0;
    }

    public WebServiceTask(List<String> commandList, ResultCallBack callback,
                          List<HashMap<String, String>> paramList) {
        this.mCommandList = commandList;
        this.mCallBack = callback;
        this.mParamList = paramList;

        m_nRequestMode = 1;
    }

    @Override
    protected String doInBackground(String... params) {
        if (m_nRequestMode == 0)
            return request(m_ServerURL, mParam);
        else {
            String result = "";
            for (int i = 0; i < mParamList.size(); i++) {
                result = request("", mParamList.get(i));
                getRequestResult(result);
            }

            return result;
        }
    }

    private String request(String url, HashMap<String, String> param) {
        boolean isOfflineEnabled = false;
        HashMap<String, String> hashMap = new HashMap<String, String>();


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

        if (NetworkUtils.isInternateAvaliable() == false) {
            if (isOfflineEnabled == true)
                return loadHttpResponseResult(hashMap, url);
            else
                return "";
        }

        String[] results = WebHttpUtils.postDataWithCookie(
                url, hashMap, "");

        if (results == null)        // not receive Http response
        {
            if (isOfflineEnabled == true)
                return loadHttpResponseResult(hashMap, url);
            else
                return "";
        } else {
            if (isOfflineEnabled == true && CheckUtils.isEmpty(results[0]) == false)
                saveHttpResponseResult(hashMap, url, results[0]);
        }

        return results[0];
    }

    private void getRequestResult(String result) {
        LogicResult serverResult = new LogicResult();
        if (result == null) {
            serverResult.mResult = LogicResult.RESULT_FAIL;
            serverResult.mMessage = "Server is not connected";
            Log.e("marsor", serverResult.mMessage);
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }

        serverResult.mResult = LogicResult.RESULT_OK;
        serverResult.mMessage = result;
        if (mCallBack != null) mCallBack.doAction(serverResult);

        super.onPostExecute(result);
    }

    @Override
    protected void onPostExecute(String result) {
        if (m_nRequestMode != 0)
            return;

        getRequestResult(result);

        super.onPostExecute(result);
    }

    private static void saveHttpResponseResult(HashMap<String, String> requestMap, String action, String response) {
        if (response.isEmpty())
            return;

        String key = "" + action;
        if (requestMap != null && requestMap.size() != 0) {
            for (String strName : requestMap.keySet()) {
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
                key += (strName + requestMap.get(strName));
            }
        }

        if (key.isEmpty())
            return response;

        response = DataUtils.getPreference(key, "");

        return response;
    }

}

