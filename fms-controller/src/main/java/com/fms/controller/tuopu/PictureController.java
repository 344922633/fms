package com.fms.controller.tuopu;

import com.alibaba.druid.stat.TableStat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.schema.ColumnInfo;
import com.fms.domain.schema.TableInfo;
import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.ControlProperty;
import com.fms.domain.tuopu.Picture;
import com.fms.service.filemanage.FileParserService;
import com.fms.service.schema.ColumnInfoService;
import com.fms.service.schema.SchemaService;
import com.fms.service.tuopu.ControlPropertyService;
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
    private ColumnInfoService columnInfoService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ControlPropertyService controlPropertyService;

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

////主从表字段信息分类后卡夫卡格式
//    @ResponseBody
//    @RequestMapping("sendDataToKafka")
//    public void sendDataToKafka(HttpServletRequest request) throws IOException {
////        String jsonData = "{\n" +
////                "    \"datas\": [\n" +
////                "        {\n" +
////                "            \"_className\": \"Q.Node\",\n" +
////                "            \"json\": {\n" +
////                "                \"image\": \"/fms/static/img/1550803183632.png\",\n" +
////                "                \"location\": {\n" +
////                "                    \"x\": -308.27981305673757,\n" +
////                "                    \"y\": -219.3316180560916\n" +
////                "                },\n" +
////                "                \"name\": \"核心路由器\",\n" +
////                "                \"properties\": {\n" +
////                "                    \"id\": \"1550803184720\",\n" +
////                "                    \"table1\": {\n" +
////                "                        \"DXBM\": \"12331\",\n" +
////                "                        \"DXFL\": \"2\",\n" +
////                "                        \"tableId\": \"1550801127654\"\n" +
////                "                    },\n" +
////                "                    \"table2\": {\n" +
////                "                        \"DXMC\": \"1321321\",\n" +
////                                        "\"DXBM\": \"12331\",\n" +
////                "                        \"KZMC\": \"1321312321\",\n" +
////                "                        \"SBLB\": \"1\",\n" +
////                "                        \"tableId\": \"1550801131740\"\n" +
////                "                    }\n" +
////                "                }\n" +
////                "            }\n" +
////                "        }\n" +
////                "    ],\n" +
////                "    \"scale\": 0.8264462809917362,\n" +
////                "    \"tx\": 521.7767050055684,\n" +
////                "    \"ty\": 335.37517504635684,\n" +
////                "    \"version\": \"2.0\"\n" +
////                "}";
//
//
//
//        String jsonData = request.getParameter("json");
//
//        // 将读取的数据转换为JSONObject
//        JSONObject jsonObject = JSONObject.parseObject(jsonData);
//        JSONArray btnArray = jsonObject.getJSONArray("datas");
//
//        // 循环获取控件
//        for (int i = 0; i < btnArray.size(); i++) {
//            JSONArray column1 = new JSONArray();
//            JSONArray column2 = new JSONArray();
//            JSONObject properties = null;
//            JSONObject parent = null;
//            JSONObject table1 = null;
//            JSONObject table2 = null;
//            String str = "";
//            String refId = "";
//            JSONObject obj = new JSONObject();
//            JSONArray data = new JSONArray();
//            JSONObject obj1 = new JSONObject();
//            JSONObject obj2 = new JSONObject();
//            JSONObject columnObj1 = new JSONObject();
//            JSONObject columnObj2 = new JSONObject();
//            String table1Id = null;
//            String table2Id = null;
//            JSONObject kongjianObj = btnArray.getJSONObject(i);
//            JSONObject json = kongjianObj.getJSONObject("json");
//
//            //这个字段需要再添加
////            if (json.containsKey("parent")) {
////                parent = JSON.parseObject(json.getString("parent"));
////                refId = table1.get(parent).toString();
////            }
//
//
//            if (json.containsKey("properties")) {
//                properties = JSON.parseObject(json.getString("properties"));
//                table1 = JSON.parseObject(properties.getString("table1"));
//                table2 = JSON.parseObject(properties.getString("table2"));
//                Iterator<String> colIt1 = table1.keySet().iterator();
//                Iterator<String> colIt2 = table2.keySet().iterator();
//
//                while (colIt1.hasNext()) {
//                    String jsonKey = colIt1.next();
//                    if (jsonKey.equals("tableId")) {
//                        str = table1.get(jsonKey).toString();
//                        table1Id = String.valueOf(table1.get(jsonKey));
//                        continue;
//                    }
//                    if (jsonKey.toLowerCase().equals("dxbm")) {
//                        str = table1.get(jsonKey).toString();
//                        obj1.put("objectCode", "dxbm");
//                        obj1.put("objectCodeValue", str);
//                        continue;
//                    }
//                    JSONObject jsonCol = new JSONObject();
//                    jsonCol.put("name", jsonKey.toLowerCase());
//                    jsonCol.put("value", table1.get(jsonKey));
//                    column1.add(jsonCol);
//                }
//
//
//                obj1.put("operationType", "INSERT");
//                String tableName1 = getTableByTableId(table1Id);
//                obj1.put("schema", env.getProperty("schema"));
//                obj1.put("table",tableName1);
//                columnObj1.put("name", "dxbm");
//                columnObj1.put("value", str);
//                column1.add(columnObj1);
//                obj1.put("columns", column1);
//                data.add(obj1);
//                obj.put("data", data);
//                while (colIt2.hasNext()) {
//                    String jsonKey = colIt2.next();
//                    if (jsonKey.equals("tableId")) {
//                        str = table2.get(jsonKey).toString();
//                        table2Id = String.valueOf(table2.get(jsonKey));
//                        continue;
//                    }
//
//                    if (jsonKey.toLowerCase().equals("dxbm")) {
//                        str = table2.get(jsonKey).toString();
//                        obj2.put("objectCode", "dxbm");
//                        obj2.put("objectCodeValue", str);
//                        continue;
//                    }
//                    JSONObject jsonCol = new JSONObject();
//                    jsonCol.put("name", jsonKey.toLowerCase());
//                    jsonCol.put("value", table2.get(jsonKey));
//                    column2.add(jsonCol);
//                }
//
//                obj2.put("operationType", "INSERT");
//                String tableName2 = getTableByTableId(table2Id);
//                obj2.put("schema", env.getProperty("schema"));
//                obj2.put("table",tableName2);
//                columnObj2.put("name", "dxbm");
//                columnObj2.put("value", str);
//                column2.add(columnObj2);
//                obj2.put("columns", column2);
//                data.add(obj2);
//                obj.put("operationSource",PropertyUtil.readValue("DEFAULT_TOPIC"));
//                obj.put("data", data);
//                System.out.println(obj.toJSONString());
//                kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"), obj.toJSONString());
//            }
//        }
//        }
//
//        public String getTableByTableId(String tableId){
//            long id = Long.valueOf(tableId);
//        return fileParserService.getTableByTableId(id);
//
//        }
//
//



    @ResponseBody
    @RequestMapping("insertData")
    public Object insertData(HttpServletRequest request) throws IOException {
        Long id ;
        if(StringUtils.isNotEmpty(request.getParameter("id"))){
            id = Long.parseLong( request.getParameter("id"));
        }else{
            id = null;
        }

        if(id !=null){
            pictureService.deleteNameById(id);
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
                if (id==null) {
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
            return ExtUtil.failure("名称重复");
        }
        return ExtUtil.success("操作成功");
    }

    @RequestMapping("showPicture")
    public Picture showPicture(Long id) {
        return pictureService.get(id);
    }

    @RequestMapping("/handlePicture")
    public String handlePicture(String jsonStr) {
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

            //masterName
            String masterName = "Node";
            String image =null;
            String id =null;
            Map properties = new HashMap();
            if (kongjianObj.containsKey("masterName")) {
                masterName = kongjianObj.getString("masterName");
                if(masterName!=null) {
                    //根据名称获取所对应的图片
                    Control control = getImageByName(masterName);
                    if (control != null){
                    image = control.getImage();
                    name = masterName;
                    id = control.getId();
                    properties.put("id", id);
                }
                }
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
                y = y.multiply(new BigDecimal(-1));
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
                    if(StringUtils.isNotBlank(image)){
                        json.put("image", image);
                    }
                    if(properties.size()!=0){
                        json.put("properties", properties);
                    }
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
                    if(StringUtils.isNotBlank(image)){
                        json.put("image", image);
                    }
                    if(properties.size()!=0){
                        json.put("properties", properties);
                    }
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
                json.put("name", masterName);

                json.put("location", location);
                if(StringUtils.isNotBlank(image)){
                    json.put("image", image);
                }
                if(properties.size()!=0){
                    json.put("properties", properties);
                }
                location.put("x", x);
                location.put("y", y);
                json.put("size",sizeJson);
                sizeJson.put("width",width);
                sizeJson.put("height",height);

                datas.add(obj);
            }
        }

        pictureData.put("datas", datas);
        pictureData.put("scale", 0.8);
        pictureData.put("tx", 512.9521709668734);
        pictureData.put("ty", 521.4666034055338);
        picData=pictureData.toJSONString();
        System.out.println(pictureData.toJSONString());
        return picData;
    }




    public Control getImageByName(String masterName) {

        return pictureService.getImageByName(masterName);
    }



    //以下代码已经替换作废

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
            JSONObject obj2 = new JSONObject();
            obj.put("operationSource",PropertyUtil.readValue("OPERATION_SOURCE"));
            obj1.put("operationType", "INSERT");
            obj2.put("operationType", "INSERT");
            JSONArray columns = new JSONArray();
            JSONArray columns2 = new JSONArray();
            JSONObject properties = null;
            JSONObject columnObj1 = new JSONObject();
            JSONObject columnObj2= new JSONObject();
            String controlId = null;
            if (json.containsKey("properties")) {
                properties = JSON.parseObject(json.getString("properties"));
                Iterator<String> colIt = properties.keySet().iterator();
                Iterator<String> colIt1 = properties.keySet().iterator();
                while (colIt.hasNext()) {
                    String jsonKey = colIt.next();
                    if (jsonKey.equals("id")) {
                        controlId = properties.getString(jsonKey);
                    }
                }
                List<ControlProperty>  controlProperties =  controlService.getTablesId(controlId);

                String MtableName = columnInfoService.queryTableInfoById(Long.parseLong(controlProperties.get(0).getTableId())).getTableEnglish();

                String StableName = columnInfoService.queryTableInfoById(Long.parseLong(controlProperties.get(1).getTableId())).getTableEnglish();

                List<ControlProperty> controlPropertyList =getColumnByControlIdAndTableId(controlId,controlProperties.get(0).getTableId());
                List<ControlProperty> controlPropertyList2 =getColumnByControlIdAndTableId(controlId,controlProperties.get(1).getTableId());
//                List<String> Scolumn =getColumnByControlIdAndTableId(controlId,controlProperties.get(1).getTableId());

                while (colIt1.hasNext()) {
                    String jsonKey = colIt1.next();
                    if (jsonKey.toLowerCase().equals("dxbm")) {
                        str = properties.get(jsonKey).toString();
                        if(str!=""){
                        obj1.put("objectCode", "dxbm");
                        obj2.put("objectCode", "dxbm");
                        obj1.put("objectCodeValue", str);
                        obj2.put("objectCodeValue", str);
                        }
                    }
                    JSONObject jsonCol = new JSONObject();
                    JSONObject jsonCol2 = new JSONObject();

                    for(ControlProperty controlProperty : controlPropertyList) {

                        if (jsonKey.equals(controlProperty.getProperty())){
                            jsonCol.put("name", jsonKey.toLowerCase());
                            jsonCol.put("value", properties.get(jsonKey));
                        }
                    }
                    for(ControlProperty controlProperty2 : controlPropertyList2) {

                        if (jsonKey.equals(controlProperty2.getProperty())){
                            jsonCol2.put("name", jsonKey.toLowerCase());
                            jsonCol2.put("value", properties.get(jsonKey));
                        }
                    }

                    if(jsonCol.size()!=0){
                        columns.add(jsonCol);
                    }
                    if(jsonCol2.size()!=0) {
                        columns2.add(jsonCol2);
                    }
                }

                obj1.put("schema", env.getProperty("schema"));
                obj2.put("schema", env.getProperty("schema"));
                obj1.put("table",MtableName);
                obj2.put("table",StableName);
                obj1.put("columns", columns);
                obj2.put("columns", columns2);
                data.add(obj1);
                data.add(obj2);
                obj.put("data", data);
                System.out.println(obj.toJSONString());
                kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"), obj.toJSONString());
            }
        }
    }


    public List<ControlProperty> getColumnByControlIdAndTableId(String controlId,String tableId){
        Map<String, Object> params = new HashMap<>();
        params.put("controlId",controlId);
        params.put("tableId",tableId);
        return controlPropertyService.getColumnByControlIdAndTableId(params);

    }


}