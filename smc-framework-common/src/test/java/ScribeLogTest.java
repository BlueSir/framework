import com.google.common.collect.Lists;
import com.twitter.common.logging.ScribeLog;
import com.twitter.common.thrift.ThriftFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-17
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */
public class ScribeLogTest {
    private final static String f = "%d--为了配ipad mini 我决定买一款200元以内耳机，求推荐? 有没有一款应用能够告诉我，符合我这个需求的，目前降价最多的AKG耳机啊？网易惠惠貌似没有这个功能，要一个一个耳机去查。";
    private final static String category = "test";

    public static void main(String... args) throws ThriftFactory.ThriftFactoryException {
//        mulitThreadTest.start();
        singleThreadTest.start();
    }

    private static Thread singleThreadTest = new Thread() {
        ScribeLog s;

        public void run() {
            s = new ScribeLog(Lists.newArrayList(new InetSocketAddress("10.13.81.44", 1463)));
            for (int i = 0; ; i++) {
                try {
                    s.append(category, String.format(f, i));
                    if (i % 100000 == 0) {
                        Thread.sleep(30);

                        System.out.println("100000 send log: " + i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private static Thread mulitThreadTest = new Thread() {

        public void run() {
            ExecutorService e = Executors.newFixedThreadPool(4);

            for (int i = 0; i < 4; i++) {
                e.submit(new Runnable() {

                    @Override
                    public void run() {
                        final ScribeLog s = new ScribeLog(Lists.newArrayList(new InetSocketAddress("10.13.81.44", 1463)));
                        for (int i = 0; ; i++) {
                            s.append("test", String.format(f, i));
                        }
                    }
                });
            }
        }
    };
}
