package com.sohu.smc.simpledb.resources;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-29
 * Time: 下午3:16
 * To change this template use File | Settings | File Templates.
 */
public class Monitor {

    private final static Logger log = LoggerFactory.getLogger(Monitor.class.getName());

    private final static String URL = "http://10.1.36.134:8096/stats";

    public static String getData() throws Exception {
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(URL);
        httpPost.addHeader("Content-Type", "application/json");

//        StringEntity entity = new StringEntity(query, "UTF-8");
//        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        InputStreamReader reader = new InputStreamReader(httpEntity.getContent());
        char[] buff = new char[1024];
        int length = 0;
        StringBuffer sb = new StringBuffer();
        while ((length = reader.read(buff)) != -1) {
//            System.out.println(new String(buff, 0, length));
            sb.append(buff, 0, length);
        }
        httpClient.getConnectionManager().shutdown();
        return addDateTime(sb.toString());
    }

    private static String addDateTime(String str) {
        String result = str;
        try {
            JSONObject json = new JSONObject(str);

            JSONObject gauges = json.getJSONObject("gauges");
            Long time = gauges.getLong("jvm_uptime");
            time = time + gauges.getLong("jvm_start_time");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(time);
            json.put("datetime", format.format(date));

            result = json.toString();

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
