package com.sohu.smc.simpledb.action;

import com.sohu.smc.core.route.Request;
import com.sohu.smc.core.route.Response;
import com.sohu.smc.simpledb.resources.QueryLog;
import com.sohu.smc.simpledb.utils.QueryFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-15
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */
public class QueryLogAction{
    private final static Logger logger = LoggerFactory.getLogger(QueryLogAction.class.getName());

    public String queryLog(Request req, Response resp) throws Exception {

        String query = URLDecoder.decode(req.getString("q"), "UTF-8");
        JSONObject jsonObject = QueryFactory.groupBy(query);

//        query = "{" +
//                            "\"queryType\": \"groupBy\"," +
//                            "\"dataSource\": \"user_active\"," +
//                            "\"granularity\": \"all\"," +
//                            "\"dimensions\": [\"platform\", \"version\"]," +
//                            "\"aggregations\":[" +
//                                "{ \"type\": \"count\", \"name\": \"rows\"}," +
//                                "{ \"type\": \"count\", \"fieldName\": \"productid\", \"name\": \"randomNumberSum\"}" +
//                            "]," +
////                            "\"postAggregations\":[" +
////                                "{ \"type\":\"arithmetic\"," +
////                                    "\"name\":\"avg_random\"," +
////                                    "\"fn\":\"/\"," +
////                                    "\"fields\":[" +
////                                        "{\"type\":\"fieldAccess\",\"name\":\"randomNumberSum\",\"fieldName\":\"randomNumberSum\"}," +
////                                        "{\"type\":\"fieldAccess\",\"name\":\"rows\",\"fieldName\":\"rows\"}" +
////                                    "]" +
////                                 "}" +
////                            "]," +
//                            "\"intervals\":[\"2012-10-01T00:00/2020-01-01T00\"]," +
//                            "\"filter\" : {" +
//                                   "\"type\": \"selector\"," +
//                                   "\"dimension\": \"platform\"," +
//                                   "\"value\" : \"Android\"" +
//                            "}" +
//                        "}";

        System.out.println(jsonObject.toString());
        return QueryLog.queryLog(jsonObject.toString());
    }

    public String test(Request req, Response resp) throws Exception {
        return "This is test!";
    }
}
