package com.findmiin.business.local.Utility.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;


import com.findmiin.business.local.manager.utils.CheckUtils;
import com.findmiin.business.local.manager.utils.RegularMatcher;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NetworkUtils {
    public static Context m_Context = null;
    public static DownloadManager m_DownloadMng = null;

    public static void setContext(Context context) {
        m_Context = context;
        if (context == null)
            return;

//		NetworkUtils.clearDownloadFile();

//		m_DownloadMng = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static final BroadcastReceiver completeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private static final String DOWNLOAD_BASE_PATH = "smartaxa";

    public static final void clearDownloadFile() {
        File cacheDir = m_Context.getCacheDir();
        String basePath = cacheDir.getAbsolutePath() + "/" + DOWNLOAD_BASE_PATH + "/";
        File fp = new File(basePath);
        if (fp != null && fp.exists()) {
            try {
                FileUtils.deleteDirectory(fp);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    }

    public static final void deleteFile(String path) {
        if (path == null || path.length() < 1)
            return;

        File fp = new File(path);
        if (fp == null || fp.exists() == false)
            return;

        try {
            if (fp.isDirectory())
                FileUtils.deleteDirectory(fp);
            else
                FileUtils.forceDelete(fp);
        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public static String encodeURL(String url) {
        if (url == null || url.length() < 1)
            return "";

        int lastSlashPos = url.lastIndexOf('/');
        String mFileName = new String(lastSlashPos == -1
                ? url
                : url.substring(lastSlashPos + 1));

        String encodeURL = "";
        if (lastSlashPos == -1) {
            try {
                encodeURL = URLEncoder.encode(url, "utf-8").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        } else {
            try {
                encodeURL = url.substring(0, lastSlashPos + 1) + URLEncoder.encode(mFileName, "utf-8").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        }

        return encodeURL;
    }

    public static String getDownloadLocalPath(String mUrl) {
        File cacheDir = m_Context.getCacheDir();
        String basePath = cacheDir.getAbsolutePath() + "/" + DOWNLOAD_BASE_PATH + "/";

        File fp = new File(basePath);

        if (fp.exists() == false)
            new File(basePath).mkdirs();
        String tmpPath = mUrl.replace("http://", "");
        tmpPath = tmpPath.replace("/", "_");
        tmpPath = tmpPath.replace(":", "_");
        basePath += tmpPath;

        return basePath;
    }

    public static String checkDownloadFile(String url) {
        if (url == null || url.length() < 1)
            return null;

        String downloadPath = getDownloadLocalPath(url);
        if (downloadPath == null || downloadPath.length() < 1)
            return null;

        if (com.findmiin.business.local.Utility.util.FileUtils.checkExistFile(downloadPath) == false)
            return null;

        return downloadPath;
    }

    public static Object downloadFile(String url, String path, Handler handler, ResultCallBack callback) {
        if (url == null || url.length() < 1 || path == null || path.length() < 1)
            return null;

        if (com.findmiin.business.local.Utility.util.FileUtils.checkExistFile(path) == true) {
            LogicResult logicResult = new LogicResult();
            if (callback != null) {
                logicResult.mResult = LogicResult.RESULT_OK;
                logicResult.mMessage = path;

                callback.doAction(logicResult);
            }
            return null;
        }

        if (m_DownloadMng != null) {
            Uri urlToDownload = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(urlToDownload);
            request.setTitle("Downloading");
            request.setDescription("Item description");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path);
            long latestId = m_DownloadMng.enqueue(request);
            return new Long(latestId);
        } else {
//			DownloadFileTask task = new DownloadFileTask(url, path, handler, callback);
//			if (android.os.Build.VERSION.SDK_INT > 12) {
//				task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
//			} else {
//				task.execute("");
//			}
            try {
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {//HONEYCOMB
                //	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                //} else {
//                	task.execute();
                //}
            } catch (RejectedExecutionException e) {
                // This shouldn't happen, but might.
            }

            return 0;//task;
        }
    }

    public static void cancelDownload(long requestID) {
        if (m_DownloadMng != null) {
            m_DownloadMng.remove(requestID);
        }
    }

    public static String getUrlVideoRTSP(String urlYoutube) {
        String rtspUrl = "";
        try {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = extractYoutubeId(urlYoutube);
            URL url = new URL(gdy + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = documentBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            //get title
            Node title = el.getElementsByTagName("title").item(0);
            String titleStr = title.getFirstChild().getNodeValue();
            //get description
            Node content = el.getElementsByTagName("content").item(0);
            String contentStr = content.getFirstChild().getNodeValue();
            //get thumbnail
            NodeList thumbnail = el.getElementsByTagName("com.dating.reveal.media:thumbnail");
            String thumbnailStr = thumbnail.item(0).getAttributes().item(0).getNodeValue();//getNodeName().getNamedItem("url");

            NodeList list = el.getElementsByTagName("com.dating.reveal.media:content");///com.dating.reveal.media:content
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null) {
                    NamedNodeMap nodeMap = node.getAttributes();
                    HashMap<String, String> maps = new HashMap<String, String>();
                    for (int j = 0; j < nodeMap.getLength(); j++) {
                        Attr att = (Attr) nodeMap.item(j);
                        maps.put(att.getName(), att.getValue());
                    }
                    if (maps.containsKey("yt:format")) {
                        String f = maps.get("yt:format");
                        if (maps.containsKey("url")) {
                            cursor = maps.get("url");
                        }
                        if (f.equals("1"))
                            return cursor;
                    }
                }
            }
            return cursor;
        } catch (Exception ex) {
        }
        return urlYoutube;

    }

    public static String extractYoutubeId(String url) throws MalformedURLException {
        String id = null;
        try {
            String query = new URL(url).getQuery();
            if (query != null) {
                String[] param = query.split("&");
                for (String row : param) {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v")) {
                        id = param1[1];
                    }
                }
            } else {
                if (url.contains("embed")) {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        } catch (Exception ex) {
        }
        return id;
    }

    private static final String YOUTUBE_THUMNAIL_FORMAT = "http://img.youtube.com/vi/%s/%s.jpg";

    public static String getYoutubeThumnailURL(String youtube_id, int type) {
        if (youtube_id == null || youtube_id.length() < 1)
            return "";

        String thumnail_file_name = "";
        if (type == 0)
            thumnail_file_name = "default";
        else
            thumnail_file_name = type + "";

        return String.format(YOUTUBE_THUMNAIL_FORMAT, youtube_id, thumnail_file_name);
    }

    private static String getRstpLinks(String code) {
        String[] urls = new String[3];
        String link = "http://gdata.youtube.com/feeds/api/videos/" + code + "?alt=json";
        String json = getJsonString(link); // here you request from the server

        try {
            JSONObject obj = new JSONObject(json);
            String entry = obj.getString("entry");
            JSONObject enObj = new JSONObject(entry);
            String group = enObj.getString("com.dating.reveal.media$group");
            JSONObject grObj = new JSONObject(group);
            String content = grObj.getString("com.dating.reveal.media$content");
            JSONObject cntObj = new JSONObject(group);
            JSONArray array = grObj.getJSONArray("com.dating.reveal.media$content");
            for (int j = 0; j < array.length(); j++) {
                JSONObject thumbs = array.getJSONObject(j);
                String url = thumbs.getString("url");
                urls[j] = url;
                Log.d("tag", url);
                //data.setThumbUrl(thumbUrl);
            }


            Log.v("tag", content);
        } catch (Exception e) {
            Log.e("tag", e.toString());
            urls[0] = urls[1] = urls[2] = null;
        }
        return urls[2];

    }

    public static String getJsonString(String url) {
        Log.e("Request URL", url);
        StringBuilder buffer = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                br.close();

            }
        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                entity.consumeContent();
            } catch (Exception e) {
                Log.e("tag", "Exception = " + e.toString());
            }
        }

        return buffer.toString();
    }


    public static long s_nlastShow = 0;

    public static boolean isInternateAvaliable() {
        if (m_Context == null)
            return false;

        ConnectivityManager mConnectivityManager = (ConnectivityManager) m_Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean bState = false;
        if (mNetworkInfo != null) {
            bState = mNetworkInfo.isAvailable();
        }
        if (bState == false) {
            long time = System.currentTimeMillis();

//			if(  time - s_nlastShow > 1000 )
//				MessageUtils.Toast("There is no network connection");
            s_nlastShow = time;
        }
        return bState;
    }

    public static ArrayList<String> retrieveURLLinks(String text) {
        ArrayList<String> links = new ArrayList<String>();

        if (text == null || text.length() < 1)
            return links;

        String regex = RegularMatcher.URL_EXPRESS;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();

            if (urlStr.startsWith("(") == true) {
                char[] stringArray = urlStr.toCharArray();

                char[] newArray = new char[stringArray.length - 1];
                System.arraycopy(stringArray, 1, newArray, 0, stringArray.length - 1);
                urlStr = new String(newArray);
            }

            if (urlStr.endsWith(")") == true) {
                char[] stringArray = urlStr.toCharArray();

                char[] newArray = new char[stringArray.length - 1];
                System.arraycopy(stringArray, 0, newArray, 0, stringArray.length - 1);
                urlStr = new String(newArray);
            }
            links.add(urlStr);
        }
        return links;
    }

    public static ArrayList<Point> retrieveURLLinksPos(String text) {
        ArrayList<Point> links = new ArrayList<Point>();

        if (text == null || text.length() < 1)
            return links;

        String regex = RegularMatcher.URL_EXPRESS;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            Point pos = new Point();
            pos.x = m.start();
            pos.y = m.end();

            if (urlStr.startsWith("(") == true)
                pos.x++;

            if (urlStr.endsWith(")") == true)
                pos.y--;
            links.add(pos);
        }
        return links;
    }

    public static ArrayList<Point> retrieveURLEmailLinksPos(String text) {
        ArrayList<Point> links = new ArrayList<Point>();

        if (text == null || text.length() < 1)
            return links;

        String regex = RegularMatcher.URLEMAIL_PARSE_EXPRESS;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            Point pos = new Point();
            pos.x = m.start();
            pos.y = m.end();

            if (urlStr.startsWith("(") == true)
                pos.x++;
            if (urlStr.endsWith(")") == true)
                pos.y--;

            links.add(pos);
        }
        return links;
    }

    public static Spanned getStringWithLinks(String text) {
        if (text == null || text.length() < 1)
            return Html.fromHtml(text);

        int end = 0;
        String htmlContent = "";
        String nonHtml = "";
        ArrayList<Point> pos = retrieveURLEmailLinksPos(text);
        for (int i = 0; i < pos.size(); i++) {
            int x = pos.get(i).x;
            int y = pos.get(i).y;

            String link = text.substring(x, y);
            if (CheckUtils.checkURL(link) == true) {
                if (link.contains("http://") == false &&
                        link.contains("https://") == false &&
                        link.contains("ftp://") == false)
                    link = "http://" + link;
            }

            if (i == 0)
                nonHtml = text.substring(0, x);
            else
                nonHtml = text.substring(pos.get(i - 1).y, x);

            htmlContent += nonHtml + "<a href=\"";
            htmlContent += link;
            htmlContent += "\">";
            htmlContent += text.substring(x, y);
            htmlContent += "</a>";
            end = y;
        }

        htmlContent += text.substring(end, text.length());
        htmlContent = htmlContent.replaceAll("(\r\n|\n)", "<br/>");

        return Html.fromHtml(htmlContent);
    }


}
