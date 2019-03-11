package com.findmiin.business.local.Utility.util;

import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WebHttpUtils {
    public static final int Connection_Timeout = 6000;

    public static final int Socket_TimeOut = 30000;

    private static String encoding = "UTF-8";

    private static final HashMap<String, String> mpContentType = new HashMap<String, String>();

    private WebHttpUtils() {
    }

    public static String getUrlContent(String strUrl, String encoding) {
        return getUrlContent(strUrl, encoding, null);
    }

    public static String getUrlContent(String strUrl, String encoding, HashMap<String, String> headerInfos) {

        HttpURLConnection.setFollowRedirects(true);
        URL url = null;
        HttpURLConnection con = null;

        String strEncoding = null;
        try {
            int index = 0;
            while ((index = strUrl.indexOf(" ")) >= 0) {
                strUrl = strUrl.substring(0, index) + "%20" + strUrl.substring(index + 1);
            }
            url = new URL(strUrl);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE7.0;windows NT 5)");
            con.setRequestProperty("Content-Type", "text/html");
            con.setConnectTimeout(Connection_Timeout);
            con.setReadTimeout(Socket_TimeOut);

            if (headerInfos != null && headerInfos.size() > 0) {
                for (String headerKey : headerInfos.keySet()) {
                    con.setRequestProperty(headerKey, headerInfos.get(headerKey));
                }
            }

            con.connect();
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                con.disconnect();
                con = null;
                return null;
            }

            String strContentType = con.getContentType();
            if (null != strContentType && strContentType.trim().length() != 0 && strContentType.indexOf("charset=") > 0) {
                strEncoding = strContentType.substring(strContentType.indexOf("charset=") + 8);
            } else {
                strEncoding = con.getContentEncoding();
            }

            StringBuffer sbContent = new StringBuffer();
            BufferedReader bf = null;
            if (strEncoding == null || strEncoding.trim().length() == 0) {
                strEncoding = encoding;
            }
            bf = new BufferedReader(new InputStreamReader(con.getInputStream(), strEncoding));

            String strContentLine = bf.readLine();
            while (strContentLine != null) {
                sbContent.append(strContentLine);
                strContentLine = bf.readLine();
            }
            String strResult = sbContent.toString();
            url = null;
            con = null;
            return strResult;

        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static void requestUrl(String strUrl, HashMap<String, String> mapData) {
        if (mapData == null || mapData.isEmpty()) {
            return;
        }
        String params = "";
        if (strUrl.indexOf("?") > 0) {
            params = "&";
        } else {
            params = "?";
        }
        for (String key : mapData.keySet()) {
            if (mapData.get(key) != null) {
                try {
                    params += key + "=" + URLEncoder.encode(mapData.get(key), "utf-8") + "&";
                } catch (UnsupportedEncodingException e1) {
                    params += key + "=" + URLEncoder.encode(mapData.get(key)) + "&";
                }
            }
        }

        final String strThreadParams = strUrl + params;
        new Thread() {

            public void run() {
                this.setName("Please wait");
                try {
                    HttpURLConnection.setFollowRedirects(true);
                    URL url = new URL(strThreadParams);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(Socket_TimeOut);
                    connection.setConnectTimeout(Connection_Timeout);
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                        byte[] data = new byte[1024];
                        int read = 0;
                        while ((read = in.read(data)) != -1) {
                            read = read == 1 ? 1 : 2;
                        }
                        in.close();
                        connection.disconnect();
                    }
                } catch (Exception e) {
                }
            }
        }.start();

    }

    public static String postDataToUrl(String postUrl, HashMap<String, String> formData) {
        String result = "";
        try {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (String strName : formData.keySet()) {
                BasicNameValuePair nameValue = new BasicNameValuePair(strName, formData.get(strName));
                list.add(nameValue);
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            HttpPost post = new HttpPost(postUrl);
            post.setEntity(entity);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Connection_Timeout);
            HttpConnectionParams.setSoTimeout(httpParameters, Socket_TimeOut);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                StringBuffer contentBuffer = new StringBuffer();
                InputStream in = response.getEntity().getContent();

                String encoding = response.getEntity().getContentEncoding() == null ? "utf-8" : response.getEntity().getContentEncoding().getValue();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
                String inputLine = null;
                while ((inputLine = reader.readLine()) != null) {
                    contentBuffer.append(inputLine);
                    contentBuffer.append("\r\n");
                }
                in.close();
                result = contentBuffer.toString();
            } else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                Header locationHeader = response.getFirstHeader("location");
                String location = null;
                if (locationHeader != null) {
                    location = locationHeader.getValue();
                    return getUrlContent(location, "utf-8");
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] postDataWithCookie(String postUrl, HashMap<String, String> formData, String cookieSelizable) {
        String[] result = new String[2];

        try {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            if (formData != null && formData.size() != 0) {
                for (String strName : formData.keySet()) {
                    BasicNameValuePair nameValue = new BasicNameValuePair(strName, formData.get(strName));
                    list.add(nameValue);
                }
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            HttpPost post = new HttpPost(postUrl);
//            post.setHeader("Cookie", cookieSelizable);
            String token_server  = DataUtils.getPreference(Const.SERVER_TOKEN, "");
            String authorizationValue = "Bearer "+token_server;
            post.setHeader("Authorization",authorizationValue);
            post.setEntity(entity);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Connection_Timeout);
            HttpConnectionParams.setSoTimeout(httpParameters, Socket_TimeOut);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                StringBuffer contentBuffer = new StringBuffer();
                InputStream in = response.getEntity().getContent();

                String encoding = response.getEntity().getContentEncoding() == null ? "utf-8" : response.getEntity().getContentEncoding().getValue();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
                String inputLine = null;
                while ((inputLine = reader.readLine()) != null) {
                    contentBuffer.append(inputLine);
                    contentBuffer.append("\r\n");
                }
                in.close();
                result[0] = contentBuffer.toString();
            } else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                Header locationHeader = response.getFirstHeader("location");
                String location = null;
                if (locationHeader != null) {
                    location = locationHeader.getValue();
                    result[0] = getUrlContent(location, "utf-8");
                }
            }
            Header cookieHeader = response.getLastHeader("Set-Cookie");
            if (cookieHeader != null) {
                String cookie = response.getLastHeader("Set-Cookie").getValue();
                if (cookie != null && cookie.length() < 1) {
                    result[1] = cookie;
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String postDataToUrl(String postUrl, HashMap<String, Object> formData, HashMap<String, String> headerInfos) {

        if (postUrl == null || postUrl.trim().length() == 0) {
            return "";
        }
        if (formData == null || formData.size() <= 0) {
            return getUrlContent(postUrl, encoding, headerInfos);
        }

        long boundaryFlag = System.currentTimeMillis();
        String HeadBoundary = "---------------------------7d" + boundaryFlag;
        String Boundary = "--" + HeadBoundary + "\r\n";
        String EndBoundary = "--" + HeadBoundary + "--\r\n";

        String fieldModel = Boundary + "Content-Disposition: form-data; name=\"%1$s\"\r\n\r\n%2$s\r\n";
        String fieldFileModel = Boundary + "Content-Disposition: form-data; name=\"%1$s\"; filename=\"%2$s\"\r\nContent-Type: %3$s\r\n\r\n";
        String headerModel = "POST %1$s HTTP/1.1\r\n%5$sHost: %4$s\r\nContent-Type: multipart/form-data; boundary=%2$s\r\nContent-Length: %3$d\r\n\r\n";

        StringBuffer sbResult = new StringBuffer();
        try {
            long contentLength = 0;
            for (String key : formData.keySet()) {
                Object objValue = formData.get(key);
                String model = "";
                if (objValue instanceof File) {
                    File tmpFile = (File) objValue;
                    String fileName = tmpFile.getName();
                    contentLength += tmpFile.length();
                    model = String.format(fieldFileModel, key, fileName, getContentTypeBySufix(fileName));
                } else {
                    model = String.format(fieldModel, key, objValue);
                }
                contentLength += model.getBytes(encoding).length;
            }
            contentLength += EndBoundary.getBytes(encoding).length;

            URL url = new URL(postUrl);
            int port = url.getPort() == -1 ? 80 : url.getPort();
            Socket socket = getCommonSocket(url.getHost(), port);

            InputStream socketResultStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();

            String pastHeaderInfo = "";
            String headerInfoModel = "%1$s: %2$s\r\n";
            if (headerInfos != null && !headerInfos.isEmpty()) {
                for (String headerKey : headerInfos.keySet()) {
                    pastHeaderInfo += String.format(headerInfoModel, headerKey, headerInfos.get(headerKey));
                }
            }
            outStream.write(String.format(headerModel, url.getFile(), HeadBoundary, contentLength, url.getHost() + ":" + port, pastHeaderInfo)
                    .getBytes(encoding));

            for (String key : formData.keySet()) {
                Object objValue = formData.get(key);
                File tmpFile = null;
                String model = "";
                if (objValue instanceof File) {
                    tmpFile = (File) objValue;
                    model = String.format(fieldFileModel, key, tmpFile.getName(), getContentTypeBySufix(tmpFile.getName()));
                } else {
                    model = String.format(fieldModel, key, objValue);
                }
                outStream.write(model.getBytes(encoding));

                if (tmpFile != null) {
                    InputStream in = new FileInputStream(tmpFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        outStream.write(buf, 0, len);
                    }
                    in.close();
                    outStream.flush();
                    outStream.write("\r\n".getBytes(encoding));
                    outStream.flush();
                }
            }
            outStream.write(EndBoundary.getBytes(encoding));

            socket.shutdownOutput();

            BufferedReader br = new BufferedReader(new InputStreamReader(socketResultStream, encoding));
            String oneLine = br.readLine();
            boolean isRedirect = false;
            isRedirect = oneLine.matches("HTTP/1.\\d 3\\d{2}.*");
            while (oneLine != null) {
                sbResult.append(oneLine + "\r\n");
                oneLine = br.readLine();
                if (oneLine != null && oneLine.trim().length() == 0 && isRedirect) {
                    String tmp = sbResult.toString();
                    int location = tmp.toLowerCase(Locale.CHINESE).indexOf("location");
                    if (location >= 0) {
                        tmp = tmp.substring(location);
                        int newline = tmp.indexOf("\r\n");
                        if (newline >= 0) {
                            tmp = tmp.substring(newline);
                        }
                        tmp = tmp.substring(tmp.indexOf(":") + 1);
                        tmp = tmp.trim();
                        return getUrlContent(tmp, encoding);
                    }
                }

            }
            try {
                socket.shutdownInput();
            } catch (Exception e) {
            }
            socket.close();
        } catch (Exception e) {
        }

        return sbResult.toString();
    }

    public static void downloadFileUrl(String postUrl, String filePath, HashMap<String, Object> formData, HashMap<String, String>... headerInfos) {

        if (postUrl == null || postUrl.trim().length() == 0) {
            return;
        }

        long boundaryFlag = System.currentTimeMillis();
        String HeadBoundary = "---------------------------7d" + boundaryFlag;
        String Boundary = "--" + HeadBoundary + "\r\n";
        String EndBoundary = "--" + HeadBoundary + "--\r\n";

        String fieldModel = Boundary + "Content-Disposition: form-data; name=\"%1$s\"\r\n\r\n%2$s\r\n";
        String fieldFileModel = Boundary + "Content-Disposition: form-data; name=\"%1$s\"; filename=\"%2$s\"\r\nContent-Type: %3$s\r\n\r\n";
        String headerModel = "POST %1$s HTTP/1.1\r\n%5$sHost: %4$s\r\nContent-Type: multipart/form-data; boundary=%2$s\r\nContent-Length: %3$d\r\n\r\n";

        try {
            long contentLength = 0;
            for (String key : formData.keySet()) {
                Object objValue = formData.get(key);
                String model = "";
                if (objValue instanceof File) {
                    File tmpFile = (File) objValue;
                    String fileName = tmpFile.getName();
                    contentLength += tmpFile.length();
                    model = String.format(fieldFileModel, key, fileName, getContentTypeBySufix(fileName));
                } else {
                    model = String.format(fieldModel, key, objValue);
                }
                contentLength += model.getBytes(encoding).length;
            }
            contentLength += EndBoundary.getBytes(encoding).length;

            URL url = new URL(postUrl);
            int port = url.getPort() == -1 ? 80 : url.getPort();
            Socket socket = getCommonSocket(url.getHost(), port);
            InputStream socketResultStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();

            String pastHeaderInfo = "";
            String headerInfoModel = "%1$s: %2$s\r\n";
            if (headerInfos != null && headerInfos.length > 0) {
                for (HashMap<String, String> mapHeader : headerInfos) {
                    for (String headerKey : mapHeader.keySet()) {
                        pastHeaderInfo += String.format(headerInfoModel, headerKey, mapHeader.get(headerKey));
                    }
                }
            }
            outStream.write(String.format(headerModel, url.getFile(), HeadBoundary, contentLength, url.getHost() + ":" + port, pastHeaderInfo)
                    .getBytes(encoding));

            for (String key : formData.keySet()) {
                Object objValue = formData.get(key);
                File tmpFile = null;
                String model = "";
                if (objValue instanceof File) {
                    tmpFile = (File) objValue;
                    model = String.format(fieldFileModel, key, tmpFile.getName(), getContentTypeBySufix(tmpFile.getName()));
                } else {
                    model = String.format(fieldModel, key, objValue);
                }
                outStream.write(model.getBytes(encoding));

                if (tmpFile != null) {
                    InputStream in = new FileInputStream(tmpFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        outStream.write(buf, 0, len);
                    }
                    in.close();
                    outStream.flush();
                    outStream.write("\r\n".getBytes(encoding));
                    outStream.flush();
                }
            }
            outStream.write(EndBoundary.getBytes(encoding));

            File file = null;
            FileOutputStream outputStream = null;
            file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            outputStream = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int read = 0;
            String header = "";
            boolean headRemoved = false;
            while ((read = socketResultStream.read(bytes)) != -1) {
                if (!headRemoved) {
                    int i;
                    for (i = 0; i < bytes.length - 4; i++) {
                        if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                            i = i + 3;
                            break;
                        }
                    }
                    headRemoved = true;

                    header += new String(bytes);
                    if (headRemoved) {
                        int location = header.toLowerCase(Locale.getDefault()).indexOf("location");
                        int httpres = header.toLowerCase(Locale.getDefault()).indexOf("\r\n");
                        boolean isRedirect = false;
                        isRedirect = header.toLowerCase(Locale.getDefault()).substring(0, httpres).matches("(?i)HTTP/1.\\d 3\\d{2}.*");
                        if (location >= 0 && isRedirect) {
                            header = header.substring(location + "location".length());
                            location = header.indexOf(':');
                            if (location >= 0) {
                                header = header.substring(location + 1);
                                header = header.trim();
                                httpres = header.toLowerCase(Locale.getDefault()).indexOf("\r\n");
                                if (httpres >= 0) {
                                    header = header.substring(0, httpres);
                                }
                            }
                            try {
                                url = new URL(header);
                                socket.close();
                                port = url.getPort() == -1 ? 80 : url.getPort();
                                socket = getCommonSocket(url.getHost(), port);
                                outStream = socket.getOutputStream();
                                outStream.write(String.format(headerModel,
                                        url.getPath(),
                                        HeadBoundary,
                                        contentLength,
                                        url.getHost() + ":" + port,
                                        pastHeaderInfo).getBytes(encoding));
                                outStream.write(EndBoundary.getBytes(encoding));
                                socket.shutdownOutput();
                                socketResultStream = socket.getInputStream();
                                headRemoved = false;
                                header = "";
                                continue;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    outputStream.write(bytes, i + 1, read - i - 1);
                    continue;
                }
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            outputStream.close();

            socketResultStream.close();
        } catch (Exception e) {
        }

    }

    public static boolean downloadWebResource(String resUrl, String localPath) throws Exception {

        HttpURLConnection.setFollowRedirects(true);
        URL url = new URL(resUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(Socket_TimeOut);
        connection.setConnectTimeout(Connection_Timeout);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE7.0;windows NT 5)");
        connection.setRequestProperty("Content-Type", "text/html");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();

            File output = new File(localPath).getAbsoluteFile();
            if (!output.exists()) {
                File parent = output.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                output.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(output, false);
            byte[] data = new byte[1024];
            int read = 0;
            while ((read = in.read(data)) != -1) {
                out.write(data, 0, read);
            }
            out.flush();
            out.close();
            in.close();
            connection.disconnect();
        } else {
            return false;
        }
        return true;
    }

    public static String getContentTypeBySufix(String sufix) {
        String unknownType = "application/octet-stream";

        if (sufix == null || sufix.trim().length() == 0) {
            return unknownType;
        }
        if (sufix.indexOf('.') >= 0) {
            sufix = sufix.substring(sufix.lastIndexOf('.') + 1);
        }
        if (mpContentType.size() == 0) {
            InputStream in = ClassLoader.getSystemResourceAsStream("ContentTypeResource.r");
            if (in != null) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line == null || line.trim().length() == 0) {
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts == null || parts.length != 2) {
                            continue;
                        }
                        mpContentType.put(parts[0], parts[1]);
                    }
                } catch (Exception e) {
                }
            }
        }

        if (mpContentType.containsKey(sufix)) {
            return mpContentType.get(sufix);
        }

        mpContentType.put(sufix, unknownType);

        return unknownType;
    }

    private static Socket getCommonSocket(String host, int port) {
        Socket socket = new Socket();
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address, Connection_Timeout);
            socket.setSoTimeout(Socket_TimeOut);

            socket.setReceiveBufferSize(8192);
            socket.setSendBufferSize(8192);
        } catch (Exception e) {
        }
        return socket;
    }

    public static void uploadFile(String filePath, String serverUrl) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        String newName = System.currentTimeMillis() + ".html";
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);
            FileInputStream fStream = new FileInputStream(new File(filePath));
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            fStream.close();
            ds.flush();
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
        } catch (Exception e) {
        }
    }
}
