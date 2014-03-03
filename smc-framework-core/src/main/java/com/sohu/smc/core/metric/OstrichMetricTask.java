package com.sohu.smc.core.metric;

import com.twitter.ostrich.stats.Stats;
import org.codehaus.jackson.JsonGenerator;
import scala.Some;
import scala.collection.Iterator;
import scala.collection.mutable.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-30
 * Time: 上午11:06
 * To change this template use File | Settings | File Templates.
 */
public class OstrichMetricTask extends AbstractTask {

    public OstrichMetricTask(String instanceName, String ip, int port) {
        super(instanceName, ip, port);
    }

    @Override
    public void run() {

        JsonGenerator json = getJson();
        try {
            HashMap<String, Object> gauges = Stats.getGauges();
            Iterator<String> it = gauges.keysIterator();

            while (it.hasNext()) {
                String key = it.next();
                Object value = gauges.get(key);
                if (value != null && value instanceof Some) {
                    json.writeStringField(key, String.valueOf(((Some) value).get()));
                } else {
                    json.writeStringField(key, (String) value);
                }
            }

            send(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MetricType getType() {
        return MetricType.ostrich;
    }
}
