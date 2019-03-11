package com.findmiin.business.local.Utility.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.Utility.util.SocChatWebHttpUtils;
import com.findmiin.business.local.manager.utils.CheckUtils;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


@SuppressLint("NewApi")
public class SocChatServerTask extends AsyncTask<String, String, String> {
    public static final String COOKIE_KEY = "com.streamcrop.cookie";

    //	public static final String CHAT_SERVER_ADRESS = "http://192.168.1.249:3335";
//	public static final String SERVER_ADRESS = "http://192.168.1.249:3000";
    public static final String CHAT_SERVER_ADRESS = "http://192.168.1.235:3335";// "http://192.169.142.189:3335";
    public static final String SERVER_ADRESS = "";// "http://ujoin.tv";
    public static final String RTMP_SERVER = "rtmp://173.244.208.99:1935/live/";

    public static final String SERVER_URL = SERVER_ADRESS + "/";

    public final static String SECURITY_KEY = "access_token";

    private String mCommand;
    private HashMap<String, String> mParam;

    private List<String> mCommandList;
    private List<HashMap<String, String>> mParamList = null;

    private JSONObject mJsonParams;
    private Boolean mJosnRequest = false;

    private ResultCallBack mCallBack;

    private int m_nRequestMode = 0;        // 0: once mode, 1: all once mode

    private int m_nFomrat = 0;                // 0: json, 1: xml, 2: url
    private int m_nStatusCode = LogicResult.RESULT_OK;

    public SocChatServerTask(String command, ResultCallBack callback,
                             HashMap<String, String> param, int format) {
        this.mCommand = command;
        this.mCallBack = callback;
        this.mParam = param;
        this.m_nFomrat = format;

        m_nRequestMode = 0;
    }

    public SocChatServerTask(String command, ResultCallBack callback,
                             JSONObject param, int format) {
        this.mCommand = command;
        this.mCallBack = callback;
        this.mJsonParams = param;
        this.m_nFomrat = format;
        this.mJosnRequest = true;

        m_nRequestMode = 0;
    }

    public SocChatServerTask(List<String> commandList, ResultCallBack callback,
                             List<HashMap<String, String>> paramList) {
        this.mCommandList = commandList;
        this.mCallBack = callback;
        this.mParamList = paramList;

        m_nRequestMode = 1;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params[0].equals("post")) {
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

        return requestGet(mCommand, mParam);

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
                hashMap.put(pKey, param.get(key));
                Log.d("LOGCAT", pKey + ":" + param.get(key));
            }
        }

        String cookie = DataUtils.getPreference(COOKIE_KEY, "");

        String[] results = null;

        String url = SERVER_URL + command;
        if (m_nFomrat == 0) {
            if (mJosnRequest == true)
                results = SocChatWebHttpUtils.postDataWithJSON(url, mJsonParams, cookie);
            else
                results = SocChatWebHttpUtils.postDataWithJSON(url, hashMap, cookie);
        }
        if (m_nFomrat == 2) {
            results = SocChatWebHttpUtils.postDataWithURL(url, hashMap, cookie);
        }

        m_nStatusCode = Integer.parseInt(results[1]);

        if (results == null) {
            return "";
        } else {
            if (CheckUtils.isEmpty(results[1]) == false) {
                if (command.equals("LOGOUT"))
                    DataUtils.savePreference(COOKIE_KEY, "");
                else
                    DataUtils.savePreference(COOKIE_KEY, results[1]);
            }
        }

        return results[0];
    }

    private String requestGet(String command, HashMap<String, String> param) {
        HashMap<String, String> hashMap = new HashMap<String, String>();

        if (param != null) {
            param.keySet();
            for (String key : param.keySet()) {
                String pKey = key;

                hashMap.put(pKey, param.get(key));
                Log.d("LOGCAT", pKey + ":" + param.get(key));
            }
        }

        String results[] = new String[2];
        if (m_nFomrat == 0) {
            results = SocChatWebHttpUtils.getUrlContentWithJSON(SERVER_URL + command, hashMap);
        }
        if (m_nFomrat == 2) {
            results = SocChatWebHttpUtils.getUrlContent(SERVER_URL + command, hashMap);
        }

        if (results[1] != null)
            m_nStatusCode = Integer.parseInt(results[1]);

        return results[0];
    }

    private void getRequestResult(String result) {
        LogicResult serverResult = new LogicResult();
        serverResult.mResult = m_nStatusCode;

        if (CheckUtils.isNotEmpty(result))
            serverResult.mContent = "" + result;
        else
            serverResult.mContent = "";

        if (result == null) {
            serverResult.mResult = LogicResult.RESULT_FAIL;
            serverResult.mMessage = "Server is not connected.";
            Log.e("marsor", serverResult.mMessage);
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("marsor", e.getMessage(), e);
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }
        if (json != null) {
            serverResult.errorAction = json.optString("error", "");
            serverResult.mMessage = json.optString("error_description", "");
            serverResult.setData(json);

            if (mCommand.equals("token") || mCommand.equals("register")) {
                saveSecurityKey(serverResult.getData());
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

        getRequestResult(result);// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2222

        super.onPostExecute(result);
    }

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

}

