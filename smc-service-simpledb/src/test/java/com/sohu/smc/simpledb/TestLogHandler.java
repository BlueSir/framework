package com.sohu.smc.simpledb;

import com.sohu.smc.simpledb.beans.LoginfoEntry;
import com.sohu.smc.simpledb.resources.LogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-6
 * Time: 下午2:11
 * To change this template use File | Settings | File Templates.
 */
public class TestLogHandler {

    private final static Logger log = LoggerFactory.getLogger(TestLogHandler.class.getName());

    private final static String file = "E:\\files\\logs\\stat_daily_channel_v3_20130105.log";
    private final static String _file = "E:\\files\\logs\\daily_install_20130105_stat_daily_channel_v3_20130105.log";

    public static void main(String[] args) throws Exception {

//        LogHandler.format(new File(file), "daily_install", "20130105");
//        LogHandler.format(new File("E:\\files\\logs\\"), new File("E:\\files\\handled\\daily_install.csv"), "daily_install");

        List<String> records = LogHandler.getRecords("daily_install", "20130101", "20130103");
//        List<String> records = LogHandler.readRecords(new File(_file));

        //每个过滤条件格式：fieldName=fieldValue，版本过滤例外，v1=empty+v1=3.0.2:表示源版本为empty，更新版本为3.0.2
        String[] filters = {"pid=1","platform=Android","v2=3.0.2"};
        //分组条件说明：如果需要按照版本分组，版本字段应该作为分组条件数组的第一个元素，如下面的语句，按照v1（源版本）分组，数组下标=0，其余字段依次往后排
        String[] groupby = {"v1","channel"};

        long stime = System.currentTimeMillis();
        log.info("分析排序日志开始......");

        Map<String, Integer> result = LogHandler.analyze(records, filters, groupby);
//        log.info("\r\n=========排序之前的分析结果==========");
//        for(Map.Entry entry : result.entrySet()) {
//            log.info(entry.getKey() + " --> " + entry.getValue());
//        }

        List<LoginfoEntry> list = LogHandler.sort(result);

        log.info("分析排序工作耗时：" + (System.currentTimeMillis() - stime) + "ms.");
        log.info("\r\n=========排序之后的分析结果==========");
        for(LoginfoEntry entry : list) {
            log.info(entry.toString());
        }
    }

}
