package com.fms.controller.kafkaHbase;

import com.fms.utils.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yer
 * @date: 2018/11/17
 * @description kafka发送消息
 */

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {


    @Autowired
    KafkaTemplate kafkaTemplate;


    /**
     * 发送kafka
     *
     * @param topic 发送主题
     * @param data  发送内容
     * @throws Exception
     */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.GET)
    public void producer(@RequestParam String topic, @RequestParam String data) throws Exception {
        kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"), data);
    }
}
