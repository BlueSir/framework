package com.sohu.smc.jdbc;

import com.sohu.smc.common.lifecycle.SpyMemcachedFactory;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-8-8
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class MemcachedTest extends TestCase {
    public final void test_getBulk() throws Exception {
        SpyMemcachedFactory cache = new SpyMemcachedFactory("user_pool");


        TblClientInstall install1 = new TblClientInstall(1, 1, 100, "TEST_IMEI_1", "3.1", (short) 1, 1000, 9);
        TblClientInstall install2 = new TblClientInstall(2, 1, 100, "TEST_IMEI_2", "3.2", (short) 2, 1000, 9);
        TblClientInstall install3 = new TblClientInstall(3, 1, 100, "TEST_IMEI_3", "3.3", (short) 3, 1000, 9);

        cache.set("test1", 60, install1);
        cache.set("test2", 60, install2);
        cache.set("test3", 60, install3);

//        System.out.println("=======");
//        System.out.println(cache.get("test1"));

        Map<String, Object> map = cache.getSlaverClient().getBulk("test1", "test2", "test3");
//        cache.getMasterClient().getBulk("");

        System.out.println(map);

        assert map == null;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey());
        }

//        Iterator<String> it = map.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            TblClientInstall value = (TblClientInstall) map.get(key);
//            System.out.println(value.toString());
//        }


    }
}
