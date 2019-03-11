package com.findmiin.business.local.Utility.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * bo?�法?�结?�类??
 *
 * @author zhaoq
 */
public class LogicResult {
    public static final int RESULT_OK = 200;
    public static final int RESULT_MISSING_PARAMS = 100;
    public static final int RESULT_INVALID_PARAMS = 101;
    public static final int RESULT_USER_EXIST = 201;
    public static final int RESULT_NO_VERIFIED = 202;
    public static final int RESULT_STATUS_INACTIVE = 203;
    public static final int RESULT_NO_USER_EXIST = 301;
    public static final int RESULT_INVALID_PWD = 302;
    public static final int RESULT_INVALID_VCODE = 303;
    public static final int RESULT_NO_PERMISSION = 304;
    public static final int RESULT_FAIL = 401;
    public static final int RESULT_DUPLICATION = 422;
    public static final int RESULT_ERROR = 450;
    public static final int RESULT_CONNECT_FAILED = 500;


    public int mResult = -1;

    public String errorAction = "OK";
    public String mMessage = "Success";
    public String mContent = "";
    private JSONObject mData;
    private JSONArray mArray;

    public JSONObject getData() {
        if (mData == null) {
            mData = new JSONObject();
        }
        return mData;
    }

    public JSONObject getContentData() {
        return getData();
    }

    public JSONArray getContentArray() {
        if (mData == null)
            return new JSONArray();

        JSONArray content = mData.optJSONArray("content");
        if (content == null)
            return new JSONArray();

        return content;
    }

    public JSONArray getArray() {
        if (mArray == null) {
            mArray = new JSONArray();
        }
        return mArray;
    }

    public void setData(JSONObject data) {
        mData = data;
    }

    public void setArray(JSONArray data) {
        mArray = data;
    }
}
