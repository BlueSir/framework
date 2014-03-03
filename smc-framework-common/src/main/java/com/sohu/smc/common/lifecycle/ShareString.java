package com.sohu.smc.common.lifecycle;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.recipes.shared.SharedValue;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: shijinkui
 * Date: 12-7-16
 * Time: 上午11:25
 */
public class ShareString extends SharedValue {

    public ShareString(CuratorFramework client, String path, JSONObject jsonObject) {
        super(client, path, jsonObject.toString().getBytes());
    }

    public JSONObject getJSON() {

        String value = new String(getValue());
        JSONObject js = null;
        try {
            js = new JSONObject(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return js;
    }


}
