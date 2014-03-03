package com.sohu.smc.core.jdbc;

import com.twitter.ostrich.stats.Stats;
import scala.runtime.AbstractFunction0;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-8-7
 * Time: 下午6:38
 * To change this template use File | Settings | File Templates.
 */
public class TraceUtil<T> {
    private final static boolean canTrace = true;

    public final static String update_obj = "update-obj";
    public final static String query_sql = "query-sql";

    public static void setCanTrace(boolean canTrace) {
        canTrace = canTrace;
    }

    public static <T> T trace(String _key, AbstractFunction0<T> fun) {
        if (!canTrace) {
            return fun.apply();
        }

        String key = parseSql(_key);
        Stats.incr(key);
        Object obj = Stats.time(key, fun);

        if (obj == null) {
            return null;
        } else {
            return (T) obj;
        }
    }

    private static String parseSql(String sql) {
        String type = sql.substring(0, 6).trim();
        String ret = "";
        if (type.equalsIgnoreCase("select") && sql.indexOf(" FROM ") > 0) {
            ret = "select " + sql.substring(sql.indexOf(" FROM "));
        } else if (type.equalsIgnoreCase("insert")) {
            ret = sql.substring(0, sql.indexOf(" ("));
        } else if (type.equalsIgnoreCase("update")) {
            ret = sql.substring(0, sql.indexOf(" SET ") + 1);
        } else if (type.equalsIgnoreCase("delete")) {
            ret = sql.substring(0, sql.indexOf("WHERE") + 1);
        } else {
            ret = sql;
        }

        return ret;
    }

    public static void main(String... args) {
        String s = "SELECT client_id, imei, platform, platform_version, machine_id, city_id, wxh, mac, ctime, mtime FROM smc_user.tbl_client_info_9 WHERE client_id =";
        String s2 = "DELETE FROM smc_subscribe.tbl_client_subscribe_11 WHERE client_id =  and product_id=  and sub_id=";
        String s3 = "INSERT INTO smc_user.tbl_client_info_12 ( client_id, imei, platform, platform_version, machine_id, city_id, wxh, mac, ctime, mtime ) VALUES ( , , , , , , , , ,  )";
        String s4 = "UPDATE smc_user.tbl_client_info_4 SET platform = , platform_version = , machine_id = , city_id = , wxh = , mac =  WHERE client_id = :";
        System.out.println(parseSql(s));
        System.out.println(parseSql(s2));
        System.out.println(parseSql(s3));
        System.out.println(parseSql(s4));
    }

}
