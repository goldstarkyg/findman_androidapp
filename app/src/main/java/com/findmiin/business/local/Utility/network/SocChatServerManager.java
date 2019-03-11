package com.findmiin.business.local.Utility.network;

import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.utils.AndroidUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class SocChatServerManager {
    public final static String COMMAND_GET_COUNTRYLIST_ACTION = "getcountrylist";
    public final static String COMMAND_GET_USERINFO_ACTION = "getuserinfo";
    public final static String COMMAND_GET_STATICLIST_ACTION = "getstaticlist";

    private static void sendRqeust(HashMap<String, String> params, String command,
                                   ResultCallBack callBack, int format) {
        SocChatServerTask task = new SocChatServerTask(command, callBack, params, format);

        if (AndroidUtils.getAPILevel() > 12) {
            //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
            task.execute("post");
        } else {
            task.execute("post");
        }
    }

    private static void sendRqeustGet(HashMap<String, String> params, String command,
                                      ResultCallBack callBack, int format) {
        SocChatServerTask task = new SocChatServerTask(command, callBack, params, format);

        if (AndroidUtils.getAPILevel() > 12) {
            //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
            task.execute("get");
        } else {
            task.execute("get");
        }
    }

    private static void sendRqeustJSonParam(JSONObject params, String command,
                                            ResultCallBack callBack, int format) {
        SocChatServerTask task = new SocChatServerTask(command, callBack, params, format);

        if (AndroidUtils.getAPILevel() > 12) {
            //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
            task.execute("post");
        } else {
            task.execute("post");
        }
    }

    private static void sendRqeust(List<HashMap<String, String>> paramsList, List<String> commandList,
                                   ResultCallBack callBack) {
        ServerTask task = new ServerTask(commandList, callBack, paramsList);

        if (AndroidUtils.getAPILevel() > 12) {
            //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
            task.execute("");
        } else {
            task.execute("");
        }
    }

    private static void sendRqeustToOtherService(String url, HashMap<String, String> params, ResultCallBack callBack) {
        WebServiceTask task = new WebServiceTask(url, callBack, params);
        task.execute("");
    }

    public static void getToken(String token, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        sendRqeustGet(map, "Account/user/" + token, callback, 2);
    }

    public static void login(String Email, String Password, String pushkey, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("email", Email);
        map.put("password", Password);

        sendRqeustGet(map, "api/login", callback, 2);
    }

    public static void submitCategory(String userId, String category, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        sendRqeust(map, "api/setcategory/" + userId + "_" + category, callback, 2);
    }

    public static void register(String Username, String Email, String Password, String pushkey, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("username", Username);
        map.put("email", Email);
        map.put("password", Password);

        sendRqeustGet(map, "api/register", callback, 2);
    }


    public static void getLiveBroadcast(ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        sendRqeustGet(map, "api/livechannel", callback, 2);
    }


    public static void getLiveArchive(ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        sendRqeustGet(map, "api/livearchive", callback, 2);
    }

    public static void getUser(String userId, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        sendRqeustGet(map, "api/user/" + userId, callback, 2);
    }

    public static void getAllUser(ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        sendRqeustGet(map, "api/alluser", callback, 2);
    }

    public static void setShare(String userId, String shareCount, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("share_count", shareCount);

        sendRqeustGet(map, "api/share/" + userId, callback, 2);
    }


    public static void setLike(String userId, String likeCount, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("like_count", likeCount);

        sendRqeustGet(map, "api/like/" + userId, callback, 2);
    }

    public static void setViewers(String userId, String viewCount, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("view_count", viewCount);

        sendRqeustGet(map, "api/viewers/" + userId, callback, 2);
    }

    public static void updateProfile(JSONObject params, String strUserId, ResultCallBack callback) {
        sendRqeustJSonParam(params, "api/updateProfile/" + strUserId, callback, 0);
    }

    public static void uploadPhoto(String userno, String path, ResultCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
//
//		UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "api/uploadPhoto/" + userno, map, null, callBack);
//		task.execute(path);
    }

    public static void updateChannelInfo(JSONObject params, String strUserId, ResultCallBack callback) {
        sendRqeustJSonParam(params, "api/updateChannelInfo/" + strUserId, callback, 0);
    }

    public static void uploadThumbnail(String userno, String path, ResultCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
//
//		UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "api/uploadThumbnail/" + userno, map, null, callBack);
//		task.execute(path);
    }

    public static void loginFacebook(String email, String name, String id, String token, ResultCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", name);
        map.put("email", email);
        map.put("token", token);

        sendRqeustGet(map, "api/facebook_login/" + id, callBack, 2);
    }

    public static void loginGoogle(String email, String name, String id, String token, ResultCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", name);
        map.put("email", email);
        map.put("token", token);

        sendRqeustGet(map, "api/google_login/" + id, callBack, 2);
    }

}
