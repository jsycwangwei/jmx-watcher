//package com.focustech.jmx.producer;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Properties;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import kafka.javaapi.producer.Producer;
//import kafka.producer.ProducerConfig;
///**
// *
// * @author wuyafeng
// * kafka生产者,单例模式(lazy initialization holder class)
// *
// */
//public class JmxMessageProducer{
//    private final Logger log = LoggerFactory.getLogger(getClass());
//    private Producer<Integer, String> producer;
//    private String topic;
//
//    //防止在第一次初始化的时候多个线程同时执行JmxMessageProducer()构造方法
//    //造成JmxMessageProducer的属性多次初始化,这里用双重检查避免该问题
//    private JmxMessageProducer() {
//        if (null == producer) {
//            synchronized (this) {
//                if (null == producer) {
//                    try {
//                        Properties properties = new Properties();
//                        properties.load(new FileInputStream("src/main/resources/kafka.properties"));
//                        producer = new Producer<Integer, String>(new ProducerConfig(properties));
//                        this.topic = properties.getProperty("topic");
//                    }
//                    catch (FileNotFoundException e) {
//                        log.error("JmxMessageProducer::JmxMessageProducer()", e);
//                    }
//                    catch (IOException e) {
//                        log.error("JmxMessageProducer::JmxMessageProducer()", e);
//                    }
//                }
//            }
//        }
//    };
//
//    private static class JxmMessageProducerHold {
//        private static final JmxMessageProducer instance = new JmxMessageProducer();
//    }
//
//    public static JmxMessageProducer getInstance() {
//        return JxmMessageProducerHold.instance;
//    }
//
//    public Producer<Integer, String> getProducer() {
//        return producer;
//    }
//
//    public String getTopic() {
//        return topic;
//    }
//}
