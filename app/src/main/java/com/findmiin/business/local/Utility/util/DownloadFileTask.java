package com.findmiin.business.local.Utility.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadFileTask extends AsyncTask<String, String, String> {
    String mURL = "";
    String mPath = "";
    Handler mHandler = null;
    ResultCallBack mCallBack = null;
    public static final int PROC_PERCENT = 1000;
    public static final int PROC_CANCEL = 1005;
    public static final String FILE_TOTAL_SIZE = "total_size";
    public static final String FILE_PROCESS_SIZE = "proc_size";

    public DownloadFileTask(String url, String path, Handler handle, ResultCallBack callback) {
        mURL = url;
        mPath = path;
        mCallBack = callback;
        mHandler = handle;
    }

    @Override
    protected String doInBackground(String... params) {

        boolean ret = downloadFile(mURL, new File(mPath));
        if (ret == false)
            return null;
        return "";
    }


    public boolean downloadFile(String urlPath, File fileFp) {
        URL fileurl;
        int Read;
        OutputStream fos = null;
        JSONObject json = new JSONObject();

        try {
            fileurl = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) fileurl.openConnection();
            InputStream is = conn.getInputStream();
            int len = conn.getContentLength();
            if (len < 1)
                return false;
            int bufferLen = Math.min(len, 1024 * 1024);
            byte[] raster = new byte[bufferLen];

            json.put(FILE_TOTAL_SIZE, len);

            try {
                fos = (OutputStream) new BufferedOutputStream(new FileOutputStream(fileFp));
            } catch (IOException e) {
//				MessageUtils.Toast("Cannot save file to " + fileFp.getPath());
            }

            if (fos == null) {
                is.close();
                conn.disconnect();
//				MessageUtils.Toast("Cannot save file to " + fileFp.getPath());
                return false;
            }
            int procSize = 0;
            for (; ; ) {
                Read = is.read(raster, 0, bufferLen);
                if (Read <= 0) {
                    break;
                }
                fos.write(raster, 0, Read);
                if (mHandler != null) {
                    procSize += Read;
                    json.put(FILE_PROCESS_SIZE, procSize);
                    Message msg = Message.obtain();
                    msg.what = PROC_PERCENT;
                    msg.obj = json;
                    mHandler.sendMessage(msg);
                }
            }

            is.close();
            fos.close();
            conn.disconnect();
        } catch (Exception e) {
//			MessageUtils.Toast(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(String result) {
        LogicResult logicResult = new LogicResult();
        if (result == null) {
            logicResult.mResult = LogicResult.RESULT_FAIL;
            if (mCallBack != null) {
                mCallBack.doAction(logicResult);
                super.onPostExecute(result);
                return;
            }
        }

        logicResult.mResult = LogicResult.RESULT_OK;
        logicResult.mMessage = mPath;
        if (mCallBack != null) {
            mCallBack.doAction(logicResult);
            return;
        }


        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        NetworkUtils.deleteFile(mPath);
        Message msg = Message.obtain();
        msg.what = PROC_CANCEL;
        mHandler.sendMessage(msg);
    }


}
