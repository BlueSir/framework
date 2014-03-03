package com.sohu.smc.simpledb.resources;

import com.sohu.smc.simpledb.beans.LoginfoEntry;
import com.sohu.smc.simpledb.utils.Common;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-6
 * Time: 上午10:49
 * To change this template use File | Settings | File Templates.
 */
public class LogHandler {

    private final static Logger log = LoggerFactory.getLogger(LogHandler.class.getName());
    private final static String key_value_separator = ",";

    private final static String SORTDB_HOST = "10.13.81.93";
    private final static int SORTDB_PORT = 9090;

    private static Map<String, Integer> fields = new HashMap<String, Integer>();
    static {
        fields.put("daily_install", 5);
        fields.put("daily_new", 6);
        fields.put("daily_lost", 7);
        fields.put("daily_connect", 8);
        fields.put("daily_visit", 9);
        fields.put("week_connect", 10);
        fields.put("week_visit", 11);
        fields.put("not_connect", 12);
        fields.put("total_connect", 13);
        fields.put("stat_date", 14);
    }

    public static void format(File target, File dest, String metric) throws Exception {
        if(target == null || dest == null)  {
            log.error("parameter is not correct!");
            return;
        }
        if(metric == null || "".equals(metric = StringUtils.trim(metric))) {
            log.error("metric is not correct!");
            return;
        }

        if(target.isDirectory()) {
            File[] files = target.listFiles();
            for(File file : files) {
                appendLogs(file, dest, metric);
                log.info("File '"+file.getName()+"' has handled.");
            }
        }else if(target.isFile()) {
            appendLogs(target, dest, metric);
        }

    }

    public static void appendLogs(File target, File dest, String metric) throws Exception {
        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            br = new BufferedReader(new FileReader(target));
            bw = new BufferedWriter(new FileWriter(dest, true));

            Map<String, String>  cache = new HashMap<String, String> ();

            String temp = br.readLine();
            while(temp != null) {
//                log.info(temp);
                String[]  info = StringUtils.split(temp, "\t");
                /**
                 * product_id, channel, platform, src_version, dst_version, daily_install, daily_new, daily_lost,
                 * daily_connect, daily_visit, week_connect, week_visit, not_connect, total_connect, stat_date
                 */
                StringBuffer keyBuf = new StringBuffer();
                StringBuffer valBuf = new StringBuffer();

                String number = info[fields.get(metric)];

                if(!"0".equals(number)) {
                    keyBuf.append("U|"+metric+".").append(Common.strTo36Digit(StringUtils.substring(info[14], 2))).append(Common.get36Digit(25))
                            .append(".pid=").append(info[0]).append(".channel=").append(info[1]).append(".platform=").append(info[2]);
                    valBuf.append(info[3]).append("+").append(info[4]).append("=").append(number);

                    String key = keyBuf.toString();
                    if(cache.containsKey(key)) {
                        String value = cache.get(key);
                        cache.put(key, valBuf.insert(0, value+" ").toString());
                    }else{
                        cache.put(key, valBuf.toString());
                    }
                }

                temp = br.readLine();
            }

            writeRecords(bw, cache);

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(br != null) br.close();
            if(bw != null) bw.close();
        }
    }

    public static List<String> getRecords(String metric, String start_date, String end_date) throws Exception {
        List<String> records = new LinkedList<String>();

        String start_date36Digit = Common.strTo36Digit(StringUtils.substring(start_date, 2) + "25");
        String end_date36Digit = Common.strTo36Digit(StringUtils.substring(end_date, 2) + "25");

        HttpClient httpClient = new DefaultHttpClient();
        String value = "U|"+metric;
        URI uri = URIUtils.createURI("http", SORTDB_HOST, SORTDB_PORT, "/fwmatch", "key="+URLEncoder.encode(value,"UTF-8"), null);
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if(entity != null) {
            InputStream instream = entity.getContent();
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                String temp;
                while((temp = br.readLine()) != null) {
                    temp = StringUtils.trim(temp);
                    if(StringUtils.isNotBlank(temp)) {
                        int index1 = temp.indexOf(".");
                        int index2 = temp.indexOf(".", index1+1);
                        String date = StringUtils.substring(temp, index1+1, index2);

                        //sortdb按照key值顺序排列记录，当
                        if(date.compareTo(end_date36Digit) == 1) {
                            break;
                        }

                        if(date.compareTo(start_date36Digit) >= 0 && date.compareTo(end_date36Digit) <= 0) {
                            records.add(temp);
                        }

                    }
                }
            }catch (RuntimeException ex) {
                httpGet.abort();
                throw ex;
            }finally {
                instream.close();
            }

            httpClient.getConnectionManager().shutdown();
        }


        return records;
    }

    public static List<String> getRecords(String metric, String date) throws Exception {

        List<String> records = new LinkedList<String>();

        HttpClient httpclient = new DefaultHttpClient();
        String value = "U|"+metric+"."+ Common.strTo36Digit(StringUtils.substring(date, 2) + "25");
        URI uri = URIUtils.createURI("http", SORTDB_HOST, SORTDB_PORT, "/fwmatch", "key="+ URLEncoder.encode(value, "UTF-8") , null);
        HttpGet httpget = new HttpGet(uri);
//        HttpGet httpget = new HttpGet("http://10.13.81.93:9090/fwmatch?key=U." + metric + "." + Common.strTo36Digit(StringUtils.substring(date, 2) + "25"));
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                String temp;
                while((temp = br.readLine()) != null) {
                    records.add(temp);
                }
            }catch (RuntimeException ex) {
                httpget.abort();
                throw ex;
            }finally {
                instream.close();
            }

            httpclient.getConnectionManager().shutdown();
        }

        return records;
    }

    public static void formatDir(File dir, String metric) throws Exception {
        if(dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(File file : files) {
                LogHandler.format(file, metric);
                System.out.println("File '"+file.getName()+"' has formatted.");
            }
        }
    }


    public static void format(File target, String metric, String date) throws Exception {
        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            if(target.exists() && target.isFile()) {

                String fileName = target.getName();
                String filePath = target.getParent();

                br = new BufferedReader(new FileReader(target));

                date = StringUtils.trim(date);
                File destFile = new File(filePath + "\\"+ metric + "_" + date + "_" + fileName);
                bw = new BufferedWriter(new FileWriter(destFile));

                //计算时间的36进制表达式
                String date36 = Common.strTo36Digit(StringUtils.substring(date, 2) + "25");

                Map<String, String> cache = new HashMap<String, String>();

                String temp = br.readLine();
                while(temp != null && StringUtils.isNotBlank(temp)) {

                    String[]  info = StringUtils.split(temp, "\t");
                    /**
                     * product_id, channel, platform, src_version, dst_version, daily_install, daily_new, daily_lost,
                     * daily_connect, daily_visit, week_connect, week_visit, not_connect, total_connect, stat_date
                     */
                    StringBuffer keyBuf = new StringBuffer();
                    StringBuffer valBuf = new StringBuffer();

                    String number = info[fields.get(metric)];

                    if(!"0".equals(number) && info[fields.get("stat_date")].equals(date)) {
                        keyBuf.append("U|" + metric + ".").append(date36)
                                .append(".pid=").append(info[0]).append(".channel=").append(info[1]).append(".platform=").append(info[2]);
                        valBuf.append(info[3]).append("+").append(info[4]).append("=").append(number);

                        String key = keyBuf.toString();
                        if(cache.containsKey(key)) {
                            String value = cache.get(key);
                            cache.put(key, valBuf.insert(0, value+" ").toString());
                        }else{
                            cache.put(key, valBuf.toString());
                        }
                    }
                    temp = br.readLine();
                }
                writeRecords(bw, cache);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(br != null) br.close();
            if(bw != null) bw.close();
        }
    }

    public static void format(File target, String metric, String start_date, String end_date) throws Exception {
        if(start_date == null || "".equals(start_date = StringUtils.trim(start_date))) {
            start_date = "19700101";
        }
        if(end_date == null || "".equals(end_date = StringUtils.trim(end_date))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            end_date = dateFormat.format(new Date());
        }

        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            if(target.exists() && target.isFile()) {

                String fileName = target.getName();
                String filePath = target.getParent();

                br = new BufferedReader(new FileReader(target));

                File destFile = new File(filePath + "\\"+ metric + "_" + start_date + "_" + end_date + "_" + fileName);
                bw = new BufferedWriter(new FileWriter(destFile));

                Map<String, String> cache = new HashMap<String, String>();

                String temp = br.readLine();
                while(temp != null && StringUtils.isNotBlank(temp)) {

                    String[]  info = StringUtils.split(temp, "\t");
                    /**
                     * product_id, channel, platform, src_version, dst_version, daily_install, daily_new, daily_lost,
                     * daily_connect, daily_visit, week_connect, week_visit, not_connect, total_connect, stat_date
                     */
                    StringBuffer keyBuf = new StringBuffer();
                    StringBuffer valBuf = new StringBuffer();

                    String number = info[fields.get(metric)];

                    if(!"0".equals(number) && info[fields.get("stat_date")].compareTo(start_date) >= 0 && info[fields.get("stat_date")].compareTo(end_date) <= 0) {
                        keyBuf.append("U|" + metric + ".").append(Common.strTo36Digit(StringUtils.substring(info[fields.get("stat_date")], 2) + "25"))
                                .append(".pid=").append(info[0]).append(".channel=").append(info[1]).append(".platform=").append(info[2]);
                        valBuf.append(info[3]).append("+").append(info[4]).append("=").append(number);

                        String key = keyBuf.toString();
                        if(cache.containsKey(key)) {
                            String value = cache.get(key);
                            cache.put(key, valBuf.insert(0, value+" ").toString());
                        }else{
                            cache.put(key, valBuf.toString());
                        }
                    }
                    temp = br.readLine();
                }
                writeRecords(bw, cache);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(br != null) br.close();
            if(bw != null) bw.close();
        }

    }

    public static void format(File target, String metric) throws Exception {

        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            if(target.exists() && target.isFile()) {

                String fileName = target.getName();
                String filePath = target.getParent();

                br = new BufferedReader(new FileReader(target));

                File destFile = new File(filePath + "\\"+ metric + "_" + fileName);
                bw = new BufferedWriter(new FileWriter(destFile));

                Map<String, String> cache = new HashMap<String, String>();

                String temp = br.readLine();
    //            int count = 1;
                while(temp != null && StringUtils.isNotBlank(temp)) {

    //                log.info("这是第"+(count++)+"条记录!");

                    String[]  info = StringUtils.split(temp, "\t");
                    /**
                     * product_id, channel, platform, src_version, dst_version, daily_install, daily_new, daily_lost,
                     * daily_connect, daily_visit, week_connect, week_visit, not_connect, total_connect, stat_date
                     */
                    StringBuffer keyBuf = new StringBuffer();
                    StringBuffer valBuf = new StringBuffer();

                    String number = info[fields.get(metric)];

                    if(!"0".equals(number)) {
                        keyBuf.append("U|"+metric+".").append(Common.strTo36Digit(StringUtils.substring(info[14], 2))).append(Common.get36Digit(25))
                                .append(".pid=").append(info[0]).append(".channel=").append(info[1]).append(".platform=").append(info[2]);
                        valBuf.append(info[3]).append("+").append(info[4]).append("=").append(number);

                        String key = keyBuf.toString();
                        if(cache.containsKey(key)) {
                            String value = cache.get(key);
                            cache.put(key, valBuf.insert(0, value+" ").toString());
                        }else{
                            cache.put(key, valBuf.toString());
                        }
                    }

                    temp = br.readLine();

                }

                writeRecords(bw, cache);

            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(br != null) br.close();
            if(bw != null) bw.close();
        }
    }

    private static void writeRecords(BufferedWriter br, Map<String, String> map) throws Exception {
        if(map != null) {
            for(Map.Entry entry : map.entrySet()) {
               br.write(entry.getKey() + key_value_separator + entry.getValue() + System.getProperty("line.separator"));
               br.flush();
            }
        }
    }

    public static List<String> readRecords(File file) throws Exception {
        BufferedReader br = null;
        List<String> records = new LinkedList<String>();
        try{
            if(file == null || !file.exists() || !file.isFile()) return records;
            br = new BufferedReader(new FileReader(file));
            String temp = br.readLine();
            while(temp != null  && StringUtils.isNotBlank(temp)) {
                records.add(temp);
                temp = br.readLine();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(br != null)  br.close();
        }
        return records;
    }

    public static Map<String, Integer> analyze(List<String> records, String[] filters, String[] groupby) throws Exception {

        Map<String, Integer> result = new HashMap<String, Integer>();

        long stime = System.currentTimeMillis();
        log.info("开始分析日志.........");

        if(records != null && records.size() > 0) {
            records = filter(records, filters);
            result = groupBy(records, filters, groupby);
        }

        log.info("日志分析结束，耗时：" + (System.currentTimeMillis() - stime) + "ms.");

        return result;
    }

    private static List<String> filter(List<String> records, String[] filters) {
        List<String> result = new LinkedList<String>();
        if(filters == null || filters.length == 0) {
            return records;
        }else{
            for(String record : records) {
                if(isFilter(record,filters)){
                    result.add(record);
                }
            }
            return result;
        }
    }

    private static boolean isFilter(String str, String[] filters) {
        boolean flag = true;

        for(String filter : filters){
            if(StringUtils.isNotBlank(filter)) {
                if(filter.indexOf("v1=") != -1 || filter.indexOf("v2=") != -1) {
                    filter = getVersionFilter(filter);
                }
                int index = StringUtils.indexOf(str,filter);
                if(index == -1) {
                    flag = false;
                }
            }
        }

        return flag;
    }

    private static String getVersionFilter(String filter) {
        String result = "";
        if(filter.indexOf("v1=") != -1 && filter.indexOf("v2=") != -1){
            String[] v_filters = StringUtils.split(filter, "\\+");
            result = StringUtils.split(v_filters[0], "=")[1] + "+" + StringUtils.split(v_filters[1], "=")[1];
        }else if(filter.indexOf("v1=") != -1) {
            result = StringUtils.split(filter, "=")[1] + "+";
        }else if(filter.indexOf("v2=") != -1) {
            result = "+" + StringUtils.split(filter, "=")[1];
        }
        return result ;
    }

    private static String getVersionFilter(String[] filters) {
        String result = "";
        if(filters == null) return result;
        for(String filter : filters) {
            if(filter.indexOf("v1=") != -1 || filter.indexOf("v2=") != -1) {
                result = getVersionFilter(filter);
            }
        }
        return result;
    }

    private static Map<String, Integer> groupBy(List<String> records, String[] filters, String[] groupby) {
        Map<String, Integer> result = new HashMap<String, Integer>();

        String key = ArrayUtils.toString(filters, "");
        key = StringUtils.substring(key, 1, key.length()-1);

        String versionFilter = getVersionFilter(filters);

        if(groupby == null || groupby.length == 0) {
            //没有groupby条件，则直接累加所有数据
            int value = 0;
            for(String record : records) {
               String[] versions = StringUtils.split(StringUtils.split(record, key_value_separator)[1]);
               for(String version : versions) {
                   if(version.indexOf(versionFilter) != -1) {
                       log.info(version);
                       String[] verInfo = StringUtils.split(version, "=");
                       if(verInfo.length == 2) {
                            value = value + Integer.parseInt(verInfo[1]);
                       }
                   }
               }
            }
            result.put(key, value);
        }else{

            String groupby1 = groupby[0];
            boolean hasVersionGroup = false;
            if(groupby1.indexOf("v1") != -1 || groupby1.indexOf("v2") != -1) {
                groupby = ArrayUtils.remove(groupby, 0);    //默认版本分组条件的index=0
                hasVersionGroup = true;
            }

            for(String record : records) {

                String _key = "";

                for(String g : groupby) {
                    String groupValue = getGroupValue(record, g);
                    if(!"".equals(groupValue)) {
                        _key = key + "," + groupValue;
                    }
                }

                String[] versions = StringUtils.split(StringUtils.split(record, key_value_separator)[1]);

                for(String version : versions) {

                    String _key_ = _key;

                    String[] verInfo = parseVersion(version);

                    if(hasVersionGroup) {
                        if("v1".equals(groupby1)) {
                            _key_ = _key_ + ",v1="+verInfo[0];
                        }else if("v2".equals(groupby1)) {
                            _key_ = _key_ + ",v2="+verInfo[1];
                        }else if("v1+v2".equals(groupby1) || "v2+v1".equals(groupby1)) {
                            _key_ = _key_ + ",v1="+verInfo[0]+",v2="+verInfo[1];
                        }
                    }

                    int count = Integer.parseInt(verInfo[2]);
                    if(result.containsKey(_key_)){
                        count = count + result.get(_key_);
                        result.put(_key_, count);
                    }else{
                        result.put(_key_, count);
                    }

                }
            }
        }
        return result;
    }

    private static String getGroupValue(String record, String groupByField) {
        String result = "";
        int index1 = record.indexOf(groupByField + "=");
        if(index1 != -1) {
            int index2 = record.indexOf(".", index1);
            if("platform".equals(groupByField)) {
                index2 = record.indexOf(key_value_separator, index1);
            }
            result = StringUtils.substring(record, index1, index2);
        }
        return result;
    }

    private static String[] parseVersion(String version) {
        String[] info = new String[3];

        String[] temp = StringUtils.split(version, "=");
        if(temp.length == 2) {
            info[2] = temp[1];

            temp = StringUtils.split(temp[0], "\\+");
            if(temp.length == 2) {
                info[0] = temp[0];
                info[1] = temp[1];
            }
        }

        return info;
    }

    /**
     * 对分析结果排序
     * @param map
     * @param reverse   false-增序，true-降序
     * @return
     */
    public static List<LoginfoEntry> sort(Map<String, Integer> map, final boolean reverse){
        List<LoginfoEntry> list = new LinkedList<LoginfoEntry>();

        try{
            if(map != null) {
                List<Map.Entry<String, Integer>> entryList = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

                Collections.sort(entryList, new Comparator(){
                    public int compare(Object obj1, Object obj2) {
                        Map.Entry<String, Integer> entry1 = (Map.Entry<String, Integer>)obj1;
                        Map.Entry<String, Integer> entry2 = (Map.Entry<String, Integer>)obj2;
                        if(reverse) {
                            return -(entry1.getValue().compareTo(entry2.getValue()));
                        }else{
                            return (entry1.getValue().compareTo(entry2.getValue()));
                        }
                    }
                });

                for(Map.Entry<String, Integer> entry : entryList) {
                    LoginfoEntry logEntry = new LoginfoEntry();
                    logEntry.setKey(entry.getKey());
                    logEntry.setValue(entry.getValue());

                    list.add(logEntry);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public static List<LoginfoEntry> sort(Map<String, Integer> map){
        return sort(map, true);
    }

}
