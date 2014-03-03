import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-12-12
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
public class ServiceTest {
    private final static Logger logger = LoggerFactory.getLogger(ServiceTest.class);

    public static void main(String... args) {
        for (int i = 0; i < 100; i++) {
            logger.info("abc" + i);
        }


//        String name = "broker-test";
//        DiscoveryServiceUtil ds = new DiscoveryServiceUtil(ServiceType.PERMANENT, "/smc/mqtt");
//        ds.removeService("broker", "10.10.76.13", 22122);
//        ds.removeService("broker", "10.10.76.13", 22123);

//        ds.removeService(name, "10.13.81.117", 22122);
//        boolean ret = ds.regist(name, "10.13.81.1172", 22122, "payload222");
//        Collection<ServiceInstance<String>> list = ds.getServiceList(name);
//        for (ServiceInstance<String> ins : list) {
//            System.out.println("======>> " + ins);
//        }
    }
}
