package com.himi.app_pailitao;

import android.net.http.Headers;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by liuchaoya on 2018/6/27.
 */
public class Tools {

    public static void main(String args[]) {
        System.out.println("Tools");
    }

    /* 设置网页抓取响应时间 */
    private static final int TIMEOUT = 10000;

    public static Map<String, Object> getHtmlScriptVars(String html, int index) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

//        InputStream io = App.THIS.getResources().getAssets().open("paitiao_result.html");
//        Document document = Jsoup.parse(io, "utf-8", "http://www.pailitao.com/");
//        Document document = Jsoup.connect(url).timeout(TIMEOUT).get();

        Document document = Jsoup.parse(html);

        System.out.println(document.title());
        // 取得script下面的JS变量
        Elements e = document.getElementsByTag("script").eq(index);
        // 循环遍历script下面的JS变量
        for (Element element : e) {
            // 取得JS变量数组
            String[] data = element.data().toString().split("var");
            // 取得单个JS变量
            for (String variable : data) {
                // 过滤variable为空的数据
                if (variable.contains("=")) {
                    // 取到满足条件的JS变量
                    if (variable.contains("option") || variable.contains("config") || variable.contains("color") || variable.contains("innerColor")) {
                        String[] kvp = variable.split("=");
                        // 取得JS变量存入map
                        if (!map.containsKey(kvp[0].trim()))
                            map.put(kvp[0].trim(), kvp[1].trim().substring(0, kvp[1].trim().length() - 1).toString());
                    }
                }
            }
        }
        return map;
    }

    public static String geturl(String actionURL) throws Exception {
        String response = "";
        try {
            URL url = new URL(actionURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求参数
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
            if (!TextUtils.isEmpty(App.cookiestr)) {
                connection.setRequestProperty("Cookie", App.cookiestr);
            }

            try {
                InputStream is = connection.getInputStream();
                String contentEncoding = connection.getHeaderField("Content-Encoding");
                if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                response = StreamUtils.formatIsToString(is);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("123", "No response get!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("123", "Request failed!");
        }
        return response;
    }

    public static String imageUpload(String actionURL, String filepath, String filename, String imagetype) throws Exception {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------------7e0dd540448";
        String response = "";
        try {
            URL url = new URL(actionURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 发送post请求需要下面两行
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 设置请求参数
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            if (!TextUtils.isEmpty(App.cookiestr)) {
                connection.setRequestProperty("Cookie", App.cookiestr);
            }
            connection.setRequestProperty("Origin", "http://www.pailitao.com");
            connection.setRequestProperty("Referer", "http://www.pailitao.com/");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
//            connection.setRequestProperty();

            // 获取请求内容输出流
            DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
//            String fileName = uploadFile.substring(uploadFile.lastIndexOf(this.PathSeparator) + 1);
            // 开始写表单格式内容
            // 写参数
//            Set<String> keys = parameters.keySet();
//            for (String key : keys) {
//                ds.writeBytes(twoHyphens + boundary + end);
//                ds.writeBytes("Content-Disposition: form-data; name=\"");
//                ds.write(key.getBytes());
//                ds.writeBytes("\"" + end);
//                ds.writeBytes(end);
//                ds.write(parameters.get(key).getBytes());
//                ds.writeBytes(end);
//            }
            // 写文件
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"imgfile\"; " + "filename=\"");
            ds.write(filename.getBytes()); // 防止中文乱码
            ds.writeBytes("\"" + end);
            ds.writeBytes("Content-Type: " + imagetype + end);
            ds.writeBytes(end);

            // 根据路径读取文件
            FileInputStream fis = new FileInputStream(filepath);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            fis.close();
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.writeBytes(end);
            ds.flush();
            try {
                InputStream is = connection.getInputStream();
                String contentEncoding = connection.getHeaderField("Content-Encoding");
                if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                response = StreamUtils.formatIsToString(is);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("123", "No response get!");
            }
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("123", "Request failed!");
        }
        return response;
    }
}
