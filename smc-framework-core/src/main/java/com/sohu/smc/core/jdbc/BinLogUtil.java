package com.sohu.smc.core.jdbc;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-10-16
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class BinLogUtil {


    public static void record(String sql) {
        record(sql, null);
    }

    public static void record(String... sqls) {
        for (String sql : sqls) {
            record(sql, null);
        }
    }

    public static void record(List<Object[]> batchArgs, String sql) {
        if (batchArgs == null || batchArgs.size() < 1) {
            record(sql, null);
        } else {
            for (Object[] parm : batchArgs) {
                record(sql, parm);
            }
        }

    }

    public static void record(String _sql, Object[] param) {
        String sql = _sql;
        if (param != null) {
            for (Object str : param) {
                sql = sql.replaceFirst("\\?", str + "");
            }
        }

        System.out.println(sql);
        //todo 1) cache; 2) insert into hbase


    }

}
