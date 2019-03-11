package com.findmiin.business.local.Utility.network;

import android.os.AsyncTask;
import android.os.Build;


import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.utils.AndroidUtils;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

public class ServerManager {
    public final static String COMMAND_GET_COUNTRYLIST_ACTION = "getcountrylist";
    public final static String COMMAND_GET_USERINFO_ACTION = "getuserinfo";
    public final static String COMMAND_GET_STATICLIST_ACTION = "getstaticlist";

    private static void sendRqeust(HashMap<String, String> params, String command,
                                   ResultCallBack callBack) {
        ServerTask task = new ServerTask(command, callBack, params);

        if (AndroidUtils.getAPILevel() > 12) {
            //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
            task.execute("");
        } else {
            task.execute("");
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



    public static void changePassword(String userid, String oldpwd, String newpwd, ResultCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("id", userid);
        map.put("password", oldpwd);
        map.put("newpass", newpwd);

        sendRqeust(map, "changePassword", callback);
    }


    public static void uploadPicture(String userid, String token, String path, String userAddress, String comment, ResultCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("userno", userid);
        map.put("token", token);
        map.put("userplace", userAddress);
        map.put("usercomment", comment);
        map.put("images", path);

        UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_UPLOAD_PHOTO_PATH, map, null, "images", callBack);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//HONEYCOMB
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                task.execute(path);
            }
        } catch (RejectedExecutionException e) {
            // This shouldn't happen, but might.
        }
    }


    /*// xcolta api////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                   //
    //                                        Local Business Api                                                 //
    //                                                                                                   //
    //////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public static void loginBusiness(String email, String password, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("password", password);
        sendRqeust(map,"auth/login",callBack);
    }

    public static void signupBusiness(String path, String firname, String lasname, String email, String password,  ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("firstname", firname);
        map.put("lastname", lasname);
        map.put("email", email);
        map.put("password", password);
        map.put("path", path);

        UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "auth/signup", map, null,"updateprofile", callBack);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//HONEYCOMB
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                task.execute(path);
            }
        } catch (RejectedExecutionException e) {
            // This shouldn't happen, but might.
        }
    }

    public static void resetPassword(String email,String  pwd, String token, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("password", pwd);
        map.put("password_confirmation", pwd);
        map.put("token", token);

        sendRqeust(map, "auth/clientreset", callBack);
    }
    public static void getCard(String mCardid, String userid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("clientid", mCardid);
        map.put("userid", userid);
        sendRqeust(map, "V1/getcard", callBack);
    }
    public static void getPost(String cardid, String sectionid,String userid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cardid", cardid);
        map.put("sectionid", sectionid);
        map.put("userid", userid);
        sendRqeust(map, "V1/getpost", callBack);
    }
    public static void getPostAll(String cardid ,String userid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cardid", cardid);
        map.put("userid", userid);
        sendRqeust(map, "V1/getpostall", callBack);
    }

    public static void postCard(String clientId, String sectionid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("clientid", clientId);
        map.put("sectionid", sectionid);
        sendRqeust(map, "V1/postcard", callBack);
    }

    public static void getSectionList(ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        sendRqeust(map, "V1/getsectionlist", callBack);
    }

    public static void getCity(ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        sendRqeust(map, "V1/getcity", callBack);
    }

    public static void getCategory(String pageNo, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pageno",pageNo);
        sendRqeust(map, "V1/getcategory", callBack);
    }

    public static void getPostWithCity(String city, String pageno, String flag,String searchWord,  ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("city",city);
        map.put("pageno",pageno);
        map.put("flag", flag);
        map.put("keyword", searchWord);
        String userid = DataUtils.getPreference(Const.USER_ID,"");
        String type = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        if(type.equals("client")){
            userid = DataUtils.getPreference(Const.CARD_ID, "");
        }
        map.put("userid", userid);
        map.put("type", type);
        sendRqeust(map, "V1/getpostwithcity", callBack);
    }

    public static void getPostWithCategory(String category, String pageno, String flag,String keyword, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("category", category);
        map.put("pageno", pageno);
        map.put("flag", flag);
        map.put("keyword", keyword);
        String userid = DataUtils.getPreference(Const.USER_ID,"");
        String type = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        if(type.equals("client")){
            userid = DataUtils.getPreference(Const.CARD_ID,"");
        }
        map.put("userid", userid);
        map.put("type", type);
        sendRqeust(map, "V1/getpostwithcategory", callBack);
    }


    public static void getPostWithSection(String section, String pageno, String flag, String keyword,   ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sectionid", section);
        map.put("pageno", pageno);
        map.put("flag",flag);
        map.put("keyword",keyword);
        String type = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        String userid = DataUtils.getPreference(Const.USER_ID,"");
        if(type.equals("client")){
            userid = DataUtils.getPreference(Const.CARD_ID,"");
        }
        map.put("userid", userid);
        map.put("type", type);
        sendRqeust(map, "V1/getpostwithsection", callBack);
    }

    public static void getProfile(String keyword,String pageno, ResultCallBack callBack){
        String userid = DataUtils.getPreference(Const.USER_ID,"");
        String type = DataUtils.getPreference(Const.LOGIN_TYPE,"");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("keyword",keyword);
        map.put("pageno", pageno);

        map.put("userid", userid);
        map.put("type", type);

        sendRqeust(map, "V1/getprofile", callBack);
    }

    public static void locationUpdate(String lat, String lon,String userid,  ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("lat", lat);
        map.put("lon", lon);
        map.put("userid", userid);
        String type = DataUtils.getPreference(Const.LOGIN_TYPE, "");
        map.put("type", type);
        sendRqeust(map, "V1/updatelocation", callBack);
    }

    public static void postedSections(String cardid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("clientid", cardid);
        sendRqeust(map, "V1/postedsections", callBack);
    }


    public static void like(String cardid, String userid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cardid", cardid);
        map.put("userid", userid);
        sendRqeust(map, "V1/like", callBack);
    }

    public static void dislike(String cardid, String userid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cardid", cardid);
        map.put("userid", userid);
        sendRqeust(map, "V1/dislike", callBack);
    }
    public static void comment(String cardid, String userid,String rating, String content, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cardid", cardid);
        map.put("userid", userid);
        map.put("rating",rating);
        map.put("content", content);
        sendRqeust(map, "V1/cardcomment", callBack);
    }
    public static void getcomment(String cardid, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cardid", cardid);
        sendRqeust(map, "V1/allcomment", callBack);
    }

    public static void getuserinfo(String userid,String pageno, String keyword, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userid", userid);
        map.put("pageno",pageno);
        map.put("keyword", keyword);
        sendRqeust(map, "V1/getuserinfo", callBack);
    }

    public static void editPostedCard(String userid, String cardid, String sectionid, String images,
                                      String infomation, String enddate, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pictures", images);
        map.put("business_information", infomation);
        map.put("contract_end_date", enddate);
        map.put("userid", userid);
        map.put("cardid", cardid);
        map.put("sectionid", sectionid);
        sendRqeust(map, "V1/postupdate", callBack);
    }
    public static void cardPhotoUpload(String path, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "V1/postimgupload", map, null,"updateprofile", callBack);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//HONEYCOMB
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                task.execute(path);
            }
        } catch (RejectedExecutionException e) {
            // This shouldn't happen, but might.
        }
    }


    public static void verifyCodeSignUpXcolta(String email, String code, String token_server, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("", code);
        map.put("", token_server);
        sendRqeust(map,"",callBack);
    }

    public static void verifyCodeForgotXcolta(String email, String code, String token_server, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("", code);
        map.put("", token_server);

        sendRqeust(map,"",callBack);
    }

    public static void forgotPasswordXcolta(String email,  ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);

        sendRqeust(map,"",callBack);
    }
    // 20170320
    public static  void uploadFile(String receive_id, String path, ResultCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("token",DataUtils.getPreference(Const.SERVER_TOKEN,""));
        map.put("user_id",DataUtils.getPreference(Const.USER_ID,""));
        map.put("receive_id", receive_id);
        map.put("recordfile", path);

        UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "V1/recordupload", map, null,"recordupload", callBack);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//HONEYCOMB
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                task.execute(path);
            }
        } catch (RejectedExecutionException e) {
            // This shouldn't happen, but might.
        }
    }


    public static void signupXcolta(String email, String password,String firstname, String last_name, String phone, String path, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("password", password);
        map.put("first_name", firstname);
        map.put("last_name", last_name);
        map.put("phone", phone);
        map.put("photo", path);

        String fcm_token  = DataUtils.getPreference(Const.FCM_TOKEN, "");
        map.put("push_key", fcm_token);

//        sendRqeust(map, "auth/signup", callBack);
        UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "auth/signup", map, null,"signup", callBack);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//HONEYCOMB
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                task.execute(path);
            }
        } catch (RejectedExecutionException e) {
            // This shouldn't happen, but might.
        }
    }

    // 2017-03-27
    public static void updateProfileXcolta(String user_id, String email, String password,String firstname, String last_name, String phone, String path, ResultCallBack callBack){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("email", email);
        map.put("password", password);
        map.put("first_name", firstname);
        map.put("last_name", last_name);
        map.put("phone", phone);
        map.put("photo", path);

        String fcm_token  = DataUtils.getPreference(Const.FCM_TOKEN, "");
        map.put("push_key", fcm_token);

        UpLoadFileTask task = new UpLoadFileTask(ServerTask.SERVER_URL + "auth/updateprofile", map, null,"updateprofile", callBack);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//HONEYCOMB
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                task.execute(path);
            }
        } catch (RejectedExecutionException e) {
            // This shouldn't happen, but might.
        }
    }

}
