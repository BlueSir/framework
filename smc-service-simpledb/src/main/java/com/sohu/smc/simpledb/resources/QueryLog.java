package com.sohu.smc.simpledb.resources;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-15
 * Time: 下午4:37
 * To change this template use File | Settings | File Templates.
 */
public class QueryLog {
    private final static Logger log = LoggerFactory.getLogger(QueryLog.class.getName());

    private final static String URL = "http://10.13.81.18:9000/druid/v2/?w";

    public static String queryLog(String query) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(URL);
        httpPost.addHeader("Content-Type", "application/json");

//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("d", query));
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");

        StringEntity entity = new StringEntity(query, "UTF-8");

        httpPost.setEntity(entity);

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
        return sb.toString();
    }
}
