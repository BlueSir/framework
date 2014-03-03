package com.sohu.smc.core.metric;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-30
 * Time: 下午7:47
 * To change this template use File | Settings | File Templates.
 */
public class JsonObject {

    private JsonGenerator json;
    private StringWriter jsonWriter;

    public JsonGenerator getJson() {
        return json;
    }

    public byte[] getContent() {
        if (jsonWriter != null) {
            return jsonWriter.getBuffer().toString().getBytes();
        }

        return null;
    }

    public void build(JsonFactory factory) {
        jsonWriter = new StringWriter();
        try {
            this.json = factory.createJsonGenerator(jsonWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            jsonWriter.flush();
            jsonWriter.close();
            json.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
