package com.fms.controller.tuopu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.tuopu.Picture;
import com.fms.service.filemanage.FileParserService;
import com.fms.service.tuopu.ControlService;
import com.fms.service.tuopu.PictureService;
import com.fms.utils.JSONUtils;
import com.fms.utils.ParamUtil;
import com.fms.utils.PropertyUtil;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.ExtUtil;
import org.apache.commons.lang3.StringUtils;

import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/picture")
public class PictureController {
    @Autowired
    private PictureService pictureService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private Environment env;
    @Autowired
    private ControlService controlService;
    @Autowired
    private FileParserService fileParserService;
    @RequestMapping(value = "testPic", method = RequestMethod.GET)
    public void testPic() {
        System.out.println("test");
    }

    /**
     * 查询拓扑图列表.
     *
     * @param params
     * @return
     */
    @RequestMapping("getPictureList")
    public Object getPictureList(Map<String, Object> params) {
        return pictureService.getList(params);
    }

    @RequestMapping("delete")
    public void delete(String id) {
        pictureService.delete(Long.parseLong(id));
    }


    /**
     * 分页查询
     *
     * @param params
     * @param request
     * @return
     */
    @RequestMapping("page")
    public Object page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Page page = ParamUtil.getPager(request);
        return pictureService.page(params, page);
    }


    @ResponseBody
    @RequestMapping("sendDataToKafka")
    public void sendDataToKafka(HttpServletRequest request) throws IOException {
        String idStr = request.getParameter("id");
        Long id = null;
        if (StringUtils.isNotEmpty(idStr)) {
            id = Long.parseLong(idStr);
        }
        String jsonData = request.getParameter("json");
        String name = request.getParameter("name");

        // 将读取的数据转换为JSONObject
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        JSONArray btnArray = jsonObject.getJSONArray("datas");

        // 循环获取控件
        for (int i = 0; i < btnArray.size(); i++) {
            String str = "";
            JSONObject kongjianObj = btnArray.getJSONObject(i);
            JSONObject json = kongjianObj.getJSONObject("json");
            JSONObject obj = new JSONObject();
            JSONArray data = new JSONArray();
            JSONObject obj1 = new JSONObject();
            obj.put("operationSource",PropertyUtil.readValue("DEFAULT_TOPIC"));
            obj1.put("operationType", "INSERT");
            JSONArray columns = new JSONArray();
            JSONObject properties = null;
            JSONObject columnObj1 = new JSONObject();
            if (json.containsKey("properties")) {
                properties = JSON.parseObject(json.getString("properties"));
                Iterator<String> colIt = properties.keySet().iterator();
                while (colIt.hasNext()) {
                    String jsonKey = colIt.next();
                    if (jsonKey.equals("id")) {
                        continue;
                    }
                    if (jsonKey.toLowerCase().equals("dxbm")) {
                        str = properties.get(jsonKey).toString();
                        obj1.put("objectCode", "dxbm");
                        obj1.put("objectCodeValue", str);
                        continue;
                    }
                    JSONObject jsonCol = new JSONObject();
                    jsonCol.put("name", jsonKey.toLowerCase());
                    jsonCol.put("value", properties.get(jsonKey));
                    columns.add(jsonCol);
                }
                String tableName = getTable(columns.toJSONString());
                obj1.put("schema", env.getProperty("schema"));
                obj1.put("table",tableName);
                columnObj1.put("name", "dxbm");
                columnObj1.put("value", str);
                columns.add(columnObj1);
                obj1.put("columns", columns);
                data.add(obj1);
                obj.put("data", data);
                System.out.println(obj.toJSONString());
                kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"), obj.toJSONString());
            }
        }
        }

        public String getTable(String str) {

            List<Map<String, Object>> json = new ArrayList<>();
            try {
                JSONArray arrayList = JSONArray.parseArray(str);
                json.addAll(JSONUtils.jsonToObject(arrayList.toJSONString(), List.class, Map.class));

            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, Object> result = fileParserService.parseData(json);
            System.out.println(result.get("table_name").toString());
            return result.get("table_name").toString();
        }


    @ResponseBody
    @RequestMapping("insertData")
    public Object insertData(HttpServletRequest request) throws IOException {
        String idStr = request.getParameter("id");
        Long id = null;
        if (StringUtils.isNotEmpty(idStr)) {
            id = Long.parseLong(idStr);
        }
        String jsonData = request.getParameter("json");
        String name = request.getParameter("name");
        //根据name判断
        if(pictureService.query(name) == 0){

        // 将读取的数据转换为JSONObject
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        Picture picture;

        if (jsonObject != null) {
            // 取出所有控件读取属性
            if (StringUtils.isEmpty(idStr)) {
                picture = new Picture();
                picture.setJson(jsonData);
                picture.setName(name);
                picture.setTime(date);
                pictureService.add(picture);

            } else {
                picture = pictureService.get(id);
                picture.setJson(jsonData);
                picture.setName(name);
                picture.setTime(date);
                pictureService.update(picture);
            }
        }
        }else{
            return ExtUtil.failure("系统出错了");
        }
    return ExtUtil.success("操作成功");
    }

    @RequestMapping("showPicture")
    public Picture showPicture(Long id) {
        return pictureService.get(id);
    }

    @RequestMapping("/handlePicture")
    public String handlePicture(String jsonStr) {
//        jsonStr="{\n" +
//                "    \"page0\": [\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"16\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"1\",\n" +
//                "            \"masterName\": \"服务器\",\n" +
//                "            \"name\": \"服务器\",\n" +
//                "            \"pinX\": \"9.224411383401687\",\n" +
//                "            \"pinY\": \"8.890051405581442\",\n" +
//                "            \"text\": \"192.168.1.20\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.7086614173228346\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"10,14\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"2\",\n" +
//                "            \"masterName\": \"PC\",\n" +
//                "            \"name\": \"PC\",\n" +
//                "            \"pinX\": \"11.44882083222059\",\n" +
//                "            \"pinY\": \"10.56327975203814\",\n" +
//                "            \"text\": \"192.168.2.55\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.984251968503937\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"12\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"3\",\n" +
//                "            \"masterName\": \"便携电脑\",\n" +
//                "            \"name\": \"便携电脑\",\n" +
//                "            \"pinX\": \"13.90945075348043\",\n" +
//                "            \"pinY\": \"10.26800416148696\",\n" +
//                "            \"text\": \"192.168.2.200\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.984251968503937\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"11,12,14,16\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"4\",\n" +
//                "            \"masterName\": \"路由器\",\n" +
//                "            \"name\": \"路由器\",\n" +
//                "            \"pinX\": \"12.13779721017334\",\n" +
//                "            \"pinY\": \"8.79162620873105\",\n" +
//                "            \"text\": \"核心路由器\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.984251968503937\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"11,13\",\n" +
//                "            \"height\": \"1.496062992125984\",\n" +
//                "            \"id\": \"5\",\n" +
//                "            \"masterName\": \"ATM 交换机\",\n" +
//                "            \"name\": \"ATM 交换机\",\n" +
//                "            \"pinX\": \"12.30512004481901\",\n" +
//                "            \"pinY\": \"6.783752192983016\",\n" +
//                "            \"text\": \"ATM交换机\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"1.122047244094488\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"13,15\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"6\",\n" +
//                "            \"masterName\": \"防火墙\",\n" +
//                "            \"name\": \"防火墙\",\n" +
//                "            \"pinX\": \"11.74409642277177\",\n" +
//                "            \"pinY\": \"4.460917547313726\",\n" +
//                "            \"text\": \"防火墙\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.5905511811023622\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"10\",\n" +
//                "            \"height\": \"0.7874015748031495\",\n" +
//                "            \"id\": \"7\",\n" +
//                "            \"masterName\": \"打印机\",\n" +
//                "            \"name\": \"打印机\",\n" +
//                "            \"pinX\": \"9.578742092063106\",\n" +
//                "            \"pinY\": \"10.76013014573892\",\n" +
//                "            \"text\": \"打印机\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.7874015748031495\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"15\",\n" +
//                "            \"height\": \"0.3937007874015748\",\n" +
//                "            \"id\": \"8\",\n" +
//                "            \"masterName\": \"调制解调器\",\n" +
//                "            \"name\": \"调制解调器\",\n" +
//                "            \"pinX\": \"9.578742092063106\",\n" +
//                "            \"pinY\": \"4.460917547313726\",\n" +
//                "            \"text\": \"Modem1\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.5511811023622047\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"\",\n" +
//                "            \"height\": \"1.250108330895425\",\n" +
//                "            \"id\": \"9\",\n" +
//                "            \"name\": \"图片9\",\n" +
//                "            \"pinX\": \"9.350122283116388\",\n" +
//                "            \"pinY\": \"6.660774862367737\",\n" +
//                "            \"text\": \"\",\n" +
//                "            \"type\": \"3\",\n" +
//                "            \"width\": \"1.008420720255643\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"2\",\n" +
//                "            \"FromId\": \"7\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"9.578742092063106\",\n" +
//                "            \"beginY\": \"10.76013014573892\",\n" +
//                "            \"connectId\": \"2,7\",\n" +
//                "            \"endX\": \"11.1535452416694\",\n" +
//                "            \"endY\": \"10.76013014573892\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"10\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.109\",\n" +
//                "            \"pinX\": \"10.36614366686625\",\n" +
//                "            \"pinY\": \"10.76013014573892\",\n" +
//                "            \"text\": \"1\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.574803149606298\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"4\",\n" +
//                "            \"FromId\": \"5\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"12.4633389351291\",\n" +
//                "            \"beginY\": \"7.329587749160263\",\n" +
//                "            \"connectId\": \"4,5\",\n" +
//                "            \"endX\": \"12.13779721017334\",\n" +
//                "            \"endY\": \"8.79162620873105\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"11\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.108\",\n" +
//                "            \"pinX\": \"12.30056807265122\",\n" +
//                "            \"pinY\": \"8.060606978945657\",\n" +
//                "            \"text\": \"5\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.497843073206032\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"3\",\n" +
//                "            \"FromId\": \"4\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"12.53149799757492\",\n" +
//                "            \"beginY\": \"9.165641956762542\",\n" +
//                "            \"connectId\": \"3,4\",\n" +
//                "            \"endX\": \"13.57763278737128\",\n" +
//                "            \"endY\": \"9.80461322225767\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"12\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.107\",\n" +
//                "            \"pinX\": \"13.0545653924731\",\n" +
//                "            \"pinY\": \"9.485127589510107\",\n" +
//                "            \"text\": \"3\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.225839417114129\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"5\",\n" +
//                "            \"FromId\": \"6\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"11.74409642277177\",\n" +
//                "            \"beginY\": \"4.460917547313726\",\n" +
//                "            \"connectId\": \"5,6\",\n" +
//                "            \"endX\": \"12.30512004481901\",\n" +
//                "            \"endY\": \"6.783752192983016\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"13\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.106\",\n" +
//                "            \"pinX\": \"12.02460823379539\",\n" +
//                "            \"pinY\": \"5.622334870148372\",\n" +
//                "            \"text\": \"6\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"2.389625137049028\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"4\",\n" +
//                "            \"FromId\": \"2\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"11.44882083222059\",\n" +
//                "            \"beginY\": \"10.56327975203814\",\n" +
//                "            \"connectId\": \"4,2\",\n" +
//                "            \"endX\": \"11.97133445682158\",\n" +
//                "            \"endY\": \"9.266347307984091\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"14\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.105\",\n" +
//                "            \"pinX\": \"11.71007764452109\",\n" +
//                "            \"pinY\": \"9.914813530011113\",\n" +
//                "            \"text\": \"2\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.398232545871275\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"6\",\n" +
//                "            \"FromId\": \"8\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"9.806375979302071\",\n" +
//                "            \"beginY\": \"4.492397583657235\",\n" +
//                "            \"connectId\": \"6,8\",\n" +
//                "            \"endX\": \"11.55905705269303\",\n" +
//                "            \"endY\": \"4.539657704794042\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"15\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.104\",\n" +
//                "            \"pinX\": \"10.68271651599755\",\n" +
//                "            \"pinY\": \"4.516027644225638\",\n" +
//                "            \"text\": \"7\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.753318129739362\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"4\",\n" +
//                "            \"FromId\": \"1\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"9.578742092063106\",\n" +
//                "            \"beginY\": \"8.79162620873105\",\n" +
//                "            \"connectId\": \"4,1\",\n" +
//                "            \"endX\": \"11.64567122592137\",\n" +
//                "            \"endY\": \"8.79162620873105\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"16\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路\",\n" +
//                "            \"pinX\": \"10.61220665899224\",\n" +
//                "            \"pinY\": \"8.79162620873105\",\n" +
//                "            \"text\": \"4\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"2.066929133858267\"\n" +
//                "        }\n" +
//                "    ],\n" +
//                "    \"page1\": [\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"9\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"1\",\n" +
//                "            \"masterName\": \"服务器\",\n" +
//                "            \"name\": \"服务器\",\n" +
//                "            \"pinX\": \"3.144094857419868\",\n" +
//                "            \"pinY\": \"3.575441122491828\",\n" +
//                "            \"text\": \"192.168.1.20\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.7086614173228346\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"6,8\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"2\",\n" +
//                "            \"masterName\": \"PC\",\n" +
//                "            \"name\": \"PC\",\n" +
//                "            \"pinX\": \"5.368504306238772\",\n" +
//                "            \"pinY\": \"5.248669468948526\",\n" +
//                "            \"text\": \"192.168.2.55\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.984251968503937\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"7\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"3\",\n" +
//                "            \"masterName\": \"便携电脑\",\n" +
//                "            \"name\": \"便携电脑\",\n" +
//                "            \"pinX\": \"7.829134227498611\",\n" +
//                "            \"pinY\": \"4.953393878397346\",\n" +
//                "            \"text\": \"192.168.2.200\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.984251968503937\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"7,8,9\",\n" +
//                "            \"height\": \"0.984251968503937\",\n" +
//                "            \"id\": \"4\",\n" +
//                "            \"masterName\": \"路由器\",\n" +
//                "            \"name\": \"路由器\",\n" +
//                "            \"pinX\": \"6.05748068419152\",\n" +
//                "            \"pinY\": \"3.477015925641436\",\n" +
//                "            \"text\": \"核心路由器\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.984251968503937\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"0\",\n" +
//                "            \"FromId\": \"0\",\n" +
//                "            \"IsLineType\": \"0\",\n" +
//                "            \"IsTwoWay\": \"0\",\n" +
//                "            \"connectId\": \"6\",\n" +
//                "            \"height\": \"0.7874015748031495\",\n" +
//                "            \"id\": \"5\",\n" +
//                "            \"masterName\": \"打印机\",\n" +
//                "            \"name\": \"打印机\",\n" +
//                "            \"pinX\": \"3.498425566081287\",\n" +
//                "            \"pinY\": \"5.445519862649306\",\n" +
//                "            \"text\": \"打印机\",\n" +
//                "            \"type\": \"0\",\n" +
//                "            \"width\": \"0.7874015748031495\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"2\",\n" +
//                "            \"FromId\": \"5\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"3.498425566081287\",\n" +
//                "            \"beginY\": \"5.445519862649306\",\n" +
//                "            \"connectId\": \"2,5\",\n" +
//                "            \"endX\": \"5.073228715687582\",\n" +
//                "            \"endY\": \"5.445519862649306\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"6\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.109\",\n" +
//                "            \"pinX\": \"4.285827140884434\",\n" +
//                "            \"pinY\": \"5.445519862649306\",\n" +
//                "            \"text\": \"1\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.574803149606295\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"3\",\n" +
//                "            \"FromId\": \"4\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"6.451181471593102\",\n" +
//                "            \"beginY\": \"3.851031673672928\",\n" +
//                "            \"connectId\": \"3,4\",\n" +
//                "            \"endX\": \"7.497316261389462\",\n" +
//                "            \"endY\": \"4.490002939168056\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"7\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.107\",\n" +
//                "            \"pinX\": \"6.974248866491282\",\n" +
//                "            \"pinY\": \"4.170517306420492\",\n" +
//                "            \"text\": \"3\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.225839417114134\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"4\",\n" +
//                "            \"FromId\": \"2\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"5.368504306238772\",\n" +
//                "            \"beginY\": \"5.248669468948526\",\n" +
//                "            \"connectId\": \"4,2\",\n" +
//                "            \"endX\": \"5.891017930839761\",\n" +
//                "            \"endY\": \"3.951737024894478\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"8\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路.105\",\n" +
//                "            \"pinX\": \"5.629761118539267\",\n" +
//                "            \"pinY\": \"4.600203246921502\",\n" +
//                "            \"text\": \"2\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"1.398232545871276\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"EndId\": \"4\",\n" +
//                "            \"FromId\": \"1\",\n" +
//                "            \"IsLineType\": \"1\",\n" +
//                "            \"IsTwoWay\": \"1\",\n" +
//                "            \"beginX\": \"3.498425566081287\",\n" +
//                "            \"beginY\": \"3.477015925641436\",\n" +
//                "            \"connectId\": \"4,1\",\n" +
//                "            \"endX\": \"5.565354699939552\",\n" +
//                "            \"endY\": \"3.477015925641436\",\n" +
//                "            \"height\": \"0.3149606299212597\",\n" +
//                "            \"id\": \"9\",\n" +
//                "            \"masterName\": \"通信链路\",\n" +
//                "            \"name\": \"通信链路\",\n" +
//                "            \"pinX\": \"4.531890133010419\",\n" +
//                "            \"pinY\": \"3.477015925641436\",\n" +
//                "            \"text\": \"4\",\n" +
//                "            \"type\": \"1\",\n" +
//                "            \"width\": \"2.066929133858265\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";
        String picData="";
        JSONObject pictureData = new JSONObject();
        pictureData.put("version", "2.0");
        JSONArray datas = new JSONArray();
        JSONObject jsonData = JSON.parseObject(jsonStr);

        String key = "";
        for(String key1 : jsonData.keySet()) {
            key = key1;
            break;
        }
        JSONArray btnArray = jsonData.getJSONArray(key);


            // 循环获取控件
        for (int i = 0; i < btnArray.size(); i++) {

            JSONObject kongjianObj = btnArray.getJSONObject(i);
            //id
            String cid = "";
            if (kongjianObj.containsKey("id")) {
                cid = kongjianObj.getString("id");
            }
            //name
            String name = "";
            if (kongjianObj.containsKey("name")) {
                name = kongjianObj.getString("name");
            }
            //pinX
            String pinX = "";
            if (kongjianObj.containsKey("pinX")) {
                pinX = kongjianObj.getString("pinX");
            }
            //pinY
            String pinY = "";
            if (kongjianObj.containsKey("pinY")) {
                pinY = kongjianObj.getString("pinY");
            }
            //IsLineType
            String IsLineType = "";
            if (kongjianObj.containsKey("IsLineType")) {
                IsLineType = kongjianObj.getString("IsLineType");
            }

            //IsTwoWay
            String IsTwoWay = "";
            if (kongjianObj.containsKey("IsTwoWay")) {
                IsTwoWay = kongjianObj.getString("IsTwoWay");
            }
            //FromId
            String FromId = "";
            if (kongjianObj.containsKey("FromId")) {
                FromId = kongjianObj.getString("FromId");
            }
            //EndId
            String EndId = "";
            if (kongjianObj.containsKey("EndId")) {
                EndId = kongjianObj.getString("EndId");
            }
            //connectId
            String connectId = "";
            if (kongjianObj.containsKey("connectId")) {
                connectId = kongjianObj.getString("connectId");
            }

            // = null 初始化
            BigDecimal x = null;
            if (kongjianObj.containsKey("pinX")) {
                x = kongjianObj.getBigDecimal("pinX").multiply(new BigDecimal(100));
            }

            BigDecimal y = null;
            if (kongjianObj.containsKey("pinY")) {
                y = kongjianObj.getBigDecimal("pinY").multiply(new BigDecimal(100));
            }

            //height
            String height = "";
            if (kongjianObj.containsKey("height")) {
                height = kongjianObj.getBigDecimal("height").multiply(new BigDecimal(15)).toString();
            }


            //width
            String width = "";
            if (kongjianObj.containsKey("width")) {
                width = kongjianObj.getBigDecimal("width").multiply(new BigDecimal(15)).toString();
            }

            if (IsLineType.equals("1")) {
                if (IsTwoWay.equals("1")) {
                    JSONObject from = new JSONObject();
                    JSONObject to = new JSONObject();
                    JSONObject styles = new JSONObject();
                    JSONObject json = new JSONObject();
                    JSONObject obj = new JSONObject();
                    obj.put("_className", "Q.Edge");
                    obj.put("json", json);
                    json.put("styles", styles);
                    styles.put("arrow.to", "true");
                    styles.put("arrow.from", "true");
                    json.put("from", from);
                    json.put("to", to);
                    from.put("_ref", FromId);
                    to.put("_ref", EndId);
                    datas.add(obj);
                } else {
                    JSONObject from = new JSONObject();
                    JSONObject to = new JSONObject();
                    JSONObject styles = new JSONObject();
                    JSONObject json = new JSONObject();
                    JSONObject obj = new JSONObject();
                    obj.put("_className", "Q.Edge");
                    obj.put("json", json);
                    json.put("styles", styles);
                    styles.put("arrow.to", "false");
                    styles.put("arrow.from", "true");
                    json.put("from", from);
                    json.put("to", to);
                    from.put("_ref", FromId);
                    to.put("_ref", EndId);
                    datas.add(obj);
                }
            } else {
                JSONObject sizeJson = new JSONObject();
                JSONObject location = new JSONObject();
                JSONObject json = new JSONObject();
                JSONObject obj = new JSONObject();
                obj.put("_className", "Q.Node");
                obj.put("_refId", cid);
                obj.put("json", json);
                json.put("name", "Node");
                json.put("location", location);
                location.put("x", x);
                location.put("y", y);
                json.put("size",sizeJson);
                sizeJson.put("width",width);
                sizeJson.put("height",height);

                datas.add(obj);
            }
        }

        pictureData.put("datas", datas);
        pictureData.put("scale", 0.5);
        pictureData.put("tx", -512.9521709668734);
        pictureData.put("ty", -521.4666034055338);
        picData=pictureData.toJSONString();
        System.out.println(pictureData.toJSONString());
        return picData;
    }



}