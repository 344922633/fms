package com.fms.controller.kafkaHbase;

import com.fms.utils.HbaseUtil;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yer
 * @date: 2018/11/17
 * @description Hbase管理
 */

@RestController
@RequestMapping(value = "/hbase")
public class HbaseController {
    @Autowired
    private Environment env;

    @RequestMapping(value = "/createTable", method = RequestMethod.GET)
    public Object createTable(@RequestParam String tableName, @RequestParam String family) throws Exception {
        HbaseUtil.getHbaseConnection(env);
        String res=HbaseUtil.createTable(tableName,family);
        HbaseUtil.close();
        return ExtUtil.success(res);
    }
}
