package com.findmiin.business.local.Utility.network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.findmiin.business.local.Utility.util.CustomMultipartEntity;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

public class UpLoadFileTask extends AsyncTask<String, String, String> {
    private ResultCallBack mCallBack;
    private HashMap<String, String> mParam = null;
    private String uploadURL = "";
    Handler mHandler = null;
    private int m_nUploadFileSize = 0;

    public static final int PROC_PERCENT = 1000;
    public static final String FILE_TOTAL_SIZE = "total_size";
    public static final String FILE_PROCESS_SIZE = "proc_size";
    String upLoadFileType;


    public UpLoadFileTask(String uploadURL, HashMap<String, String> params, Handler handle, String uploadFileType, ResultCallBack callback) {
        this.mCallBack = callback;
        this.mParam = params;

        this.uploadURL = uploadURL;
        mHandler = handle;
        upLoadFileType = uploadFileType;
    }

    @Override
    protected String doInBackground(String... params) {
        String mFilePath = params[0];
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(
                CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(uploadURL);
        File file = new File(mFilePath);
        file.setReadable(true);
        if (file != null)
            m_nUploadFileSize = (int) file.length();
        MultipartEntity mpEntity = new CustomMultipartEntity(new CustomMultipartEntity.ProgressListener() {
            @Override
            public void transferred(long transferred, long length) {

                if (mHandler != null) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put(FILE_TOTAL_SIZE, m_nUploadFileSize);
                        json.put(FILE_PROCESS_SIZE, transferred);
                        Message msg = Message.obtain();
                        msg.what = PROC_PERCENT;
                        msg.obj = json;
                        mHandler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ContentBody cbFile = new FileBody(file);

        switch (upLoadFileType) {

            case "updateprofile":
                mpEntity.addPart("photo", cbFile);
                break;

            default:
                return null;
        }

        try {

            if (mParam != null) {
                for (String key : mParam.keySet()) {
                    String pKey = key;
                    mpEntity.addPart(pKey, new StringBody(mParam.get(key), Charset.forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e1) {

            e1.printStackTrace();
        }

        String token_server  = DataUtils.getPreference(Const.SERVER_TOKEN, "");
        String authorizationValue = "Bearer "+token_server;
        httppost.setHeader("Authorization",authorizationValue);
        httppost.setEntity(mpEntity);

        HttpResponse response;
        String json = "";

        try {
            response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            System.out.println(response.getStatusLine());//

            if (resEntity != null) {
                json = EntityUtils.toString(resEntity, "utf-8");
                JSONObject p = new JSONObject(json);
                //accessPath = (String) p.get("path");
            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return json;
        } catch (Exception e) {
            Log.e("marsor", "upload file  error", e);
        }
        return json;
    }

    @Override
    protected void onPostExecute(String result) {

        LogicResult serverResult = new LogicResult();
        serverResult.mContent = result;
        if (result == null) {
            serverResult.mResult = LogicResult.RESULT_FAIL;
            serverResult.mMessage = "Server is not responding";
            Log.e("marsor", serverResult.mMessage);
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }

        int idx = 0;
        if ((idx = result.indexOf("\\")) != -1) {
            result = result.substring(0, idx) + result.substring(idx + 1, result.length());
        }
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("marsor", e.getMessage(), e);
            serverResult.mResult = LogicResult.RESULT_ERROR;
            serverResult.mMessage = "Server is not responding";
            if (mCallBack != null) mCallBack.doAction(serverResult);
            return;
        }
        if (json != null) {
            try {
                serverResult.mResult = json.getInt("status_code");
                serverResult.mMessage = json.getString("message");
                serverResult.setData(json);

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

    public static final int PROC_CANCEL = 1005;

    @Override
    protected void onCancelled() {
        Message msg = Message.obtain();
        msg.what = PROC_CANCEL;
        mHandler.sendMessage(msg);
    }

}
