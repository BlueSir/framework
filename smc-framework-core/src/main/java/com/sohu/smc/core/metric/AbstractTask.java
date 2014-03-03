package com.sohu.smc.core.metric;

import com.sohu.smc.core.udp.UDPClient;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-30
 * Time: 下午4:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTask implements Runnable {

    private final UDPClient client = new UDPClient();
    private final ObjectPool<JsonObject> objectPool = new StackObjectPool(new JSONFactory());
    private final JsonFactory jsonFactory = new JsonFactory();
    private final String instanceName, ip;
    private final int port;

    public abstract MetricType getType();

    public AbstractTask(String instanceName, String ip, int port) {
        this.instanceName = instanceName;
        this.ip = ip;
        this.port = port;
    }

    public void send(JsonGenerator json) {
        try {
            json.writeEndObject();
            json.close();
            client.send(json.getOutputTarget().toString().getBytes());
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectPool.borrowObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JsonGenerator getJson() {
        try {
            return objectPool.borrowObject().getJson();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private class JSONFactory extends BasePoolableObjectFactory<JsonObject> {

        @Override
        public JsonObject makeObject() throws Exception {

            JsonObject json = new JsonObject();
            json.build(jsonFactory);
            JsonGenerator j = json.getJson();
            j.writeStartObject();
            j.writeStringField("logType", getType().type);
            j.writeStringField("instanceName", instanceName);
            j.writeStringField("ip", ip);
            j.writeNumberField("port", port);

            return json;
        }

        @Override
        public void passivateObject(JsonObject entry) {
            try {
                entry.clear();
                JsonGenerator j = entry.getJson();
                j.writeStartObject();
                j.writeStringField("logType", getType().type);
                j.writeStringField("instanceName", instanceName);
                j.writeStringField("ip", ip);
                j.writeNumberField("port", port);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void destroyObject(JsonObject entry) {
            entry.clear();
            entry = null;
        }
    }
}
