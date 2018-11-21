package com.fms.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

/**
 * kafka消费监听
 */
public class KafkaMessageListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaMessageListener.class);

    public final CountDownLatch latch1 = new CountDownLatch(1);

    @KafkaListener(id = "0", topics = {"image","insert","log","schema"})
    public void listen1(ConsumerRecord<?, ?> record) {
        switch (record.topic()){
            case "image":
                LOGGER.info("监听到图片==》"+record.toString());
                break;
            case "insert":
                LOGGER.info("监听到插入==》"+record.toString());
                break;
            case "log":
                LOGGER.info("监听到日志==》"+record.toString());
                break;
            case "schema":
                LOGGER.info("监听到手工录入==》"+record.toString());
                break;
        }
        this.latch1.countDown();
    }

}
