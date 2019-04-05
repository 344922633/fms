package com.fms.controller.kafkaHbase;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.utils.HbaseUtil;
import com.handu.apollo.utils.ExtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
        HbaseUtil.getHbaseConnection();
        String res = HbaseUtil.createTable(tableName, family);
        HbaseUtil.close();
        return ExtUtil.success(res);
    }


    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public Object query(@RequestParam String sTime, @RequestParam String eTime) throws Exception {

        HbaseUtil.getHbaseConnection();
        JSONArray res = HbaseUtil.scanValuesByTimes("ns_fms:tb_file", string2Timestap(sTime), string2Timestap(eTime));
        HbaseUtil.close();
        return sortArr(res);
    }

    private Long string2Timestap(String s) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date sDate = simpleDateFormat.parse(s);
        Long time = sDate.getTime();

        return time;
    }

    private JSONArray sortArr(JSONArray arr){
        List<JSONObject> list = JSONArray.parseArray(arr.toJSONString(), JSONObject.class);
        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                long a = o1.getLong("timestamp");
                long b = o2.getLong("timestamp");
                if (a > b) {
                    return 1;
                } else if(a == b) {
                    return 0;
                } else
                    return -1;
            }
        });
        return  JSONArray.parseArray(list.toString());
    }
}
