package com.fms.controller.tuopu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.caeit.parser.visioParser.VisioParser;
import com.fms.domain.tuopu.Picture;
import com.fms.service.tuopu.ControlService;
import com.fms.service.tuopu.PictureService;
import com.fms.utils.ParamUtil;
import com.handu.apollo.base.Page;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

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
    private ControlService controlService;

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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());

        JSONArray btnArray = jsonObject.getJSONArray("datas");

        // 循环获取控件
        for (int i = 0; i < btnArray.size(); i++) {
            String str = "";

            JSONObject kongjianObj = btnArray.getJSONObject(i);

            JSONObject json = kongjianObj.getJSONObject("json");// "json": {
            // }

             /*   String controlName = "";
                if (json.containsKey("name")) {
                    controlName = json.getString("name");
                }*/

            JSONObject obj = new JSONObject();
            JSONArray data = new JSONArray();

            JSONObject obj1 = new JSONObject();
            obj.put("operationSource", "XX_PLATFORM");
            obj1.put("operationType", "INSERT");


            JSONArray columns = new JSONArray();

            JSONObject jo = btnArray.getJSONObject(i);
            JSONObject properties = null;
     /*           if (jo.containsKey("properties")) {
                    properties = JSON.parseObject(json.getString("properties"));
                    System.out.println(properties);
                    for (String key : properties.keySet()) {
                        JSONObject obj3 = new JSONObject();
                        obj3.put(key, properties.get(key));
                        columns.add(obj3);
                    }
                }*/
            JSONObject columnObj1 = new JSONObject();

            if (json.containsKey("properties")) {
                properties = JSON.parseObject(json.getString("properties"));

                Iterator<String> colIt = properties.keySet().iterator();

                while (colIt.hasNext()) {

                    String jsonKey = colIt.next();
                    if (jsonKey.equals("id")) {
                        continue;
                    }
                    if (jsonKey.equals("schema")) {
                        obj1.put("schema", properties.get(jsonKey).toString());
                        continue;
                    }
                    if (jsonKey.equals("table")) {
                        obj1.put("table", properties.get(jsonKey).toString());
                        continue;
                    }
                    if (jsonKey.equals("dxbm")) {
                        str= properties.get(jsonKey).toString();
                        obj1.put("objectCode", "dxbm");
                        obj1.put("objectCodeValue", str);
                        continue;
                    }
                    JSONObject jsonCol = new JSONObject();
                    jsonCol.put("name", jsonKey);
                    jsonCol.put("value", properties.get(jsonKey));
                    columns.add(jsonCol);

                }
                columnObj1.put("name", "dxbm");
                columnObj1.put("value", str);
                columns.add(columnObj1);

                obj1.put("columns", columns);
                data.add(obj1);
                obj.put("data", data);

                System.out.println(obj.toJSONString());
                kafkaTemplate.send("operation_3rd1", obj.toJSONString());
            }

        }
    }

    @ResponseBody
    @RequestMapping("insertData")
    public void insertData(HttpServletRequest request) throws IOException {
        String idStr = request.getParameter("id");
        Long id = null;
        if (StringUtils.isNotEmpty(idStr)) {
            id = Long.parseLong(idStr);
        }
        String jsonData = request.getParameter("json");
        String name = request.getParameter("name");

        // 将读取的数据转换为JSONObject
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
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
       /*     //text
            String text = "";
            if (kongjianObj.containsKey("text")) {
                text = kongjianObj.getString("text");
            }
*/
            BigDecimal x = null;// = null 初始化
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
/*
    @RequestMapping("/handlePictureFile")
    public void handlePictureFile() throws Exception {
        File testFile = new File("D:\\1127-VisioParser_testFile.vsdx");
        String fileName = testFile.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = "D:\\";

        if (suffix.equals("vsdx")) {
            Map<String, String> map = new VisioParser().parseVisio(testFile,path);
            String outPut = map.get("jsonBottomLevel");
            System.out.println(outPut);
            handlePicture(outPut);
        }*/
}