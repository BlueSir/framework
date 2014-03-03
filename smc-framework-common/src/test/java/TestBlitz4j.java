/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


//import com.netflix.blitz4j.LoggingConfiguration;

public class TestBlitz4j {
//    protected Properties props = new Properties();
//
//    @After
//    public void tearDown() throws Exception {
//        props.clear();
//    }
//
//
//    @Test
//    public void testAsyncAppenders() throws Exception {
//        props.setProperty("log4j.rootCategory", "OFF");
//        props.setProperty("log4j.logger.TestBlitz4j", "INFO,stdout");
//        props.setProperty("log4j.logger.TestBlitz4j$1", "INFO,stdout");
//        props.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
//        props.setProperty("log4j.appender.stdout.layout", "com.netflix.logging.log4jAdapter.NFPatternLayout");
//        props.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d %-5p %C:%L [%t] [%M] %m%n");
//
//        props.setProperty("log4j.logger.asyncAppenders", "INFO,stdout");
//        props.setProperty("batcher.com.netflix.logging.AsyncAppender.stdout.waitTimeinMillis", "120000");
//        int noOfThreads = 10;
//        ExecutorService es = Executors.newFixedThreadPool(noOfThreads);
//        final Logger slflogger = LoggerFactory.getLogger(TestBlitz4j.class);
//        LoggingConfiguration.getInstance().configure(props);
//
//        for (int i = 0; i < noOfThreads; i++) {
//            Thread t1 = new Thread(new Runnable() {
//                public void run() {
//                    for (int a = 0; a < 1000; a++) {
//                        slflogger.info("Testing named log with this string {}", "Test String");
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            es.submit(t1);
//        }
//
////        LoggingConfiguration.getInstance().stop();
//    }

}