package com.sohu.smc.simpledb.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-16
 * Time: 上午10:36
 * To change this template use File | Settings | File Templates.
 */
public class QueryFactory {

    public static JSONObject groupBy(String query) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(query);
            jsonObject.put("queryType", "groupBy");
            jsonObject.put("dataSource", "user_active");
            jsonObject.put("granularity", "all");
            jsonObject.append("aggregations", new JSONObject("{\"type\": \"count\", \"name\": \"rows\"}"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
