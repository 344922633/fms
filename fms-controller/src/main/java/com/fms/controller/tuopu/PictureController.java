package com.fms.controller.tuopu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.ControlProperty;
import com.fms.domain.tuopu.Picture;
import com.fms.service.tuopu.ControlService;
import com.fms.service.tuopu.PicPropertyService;
import com.fms.service.tuopu.PictureService;
import com.fms.utils.ParamUtil;
import com.handu.apollo.base.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/picture")
public class PictureController {
	@Autowired
	private PictureService pictureService;

	@Autowired
	private PicPropertyService picPropertyService;

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
        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        JSONArray data = new JSONArray();
        JSONArray columns = new JSONArray();

        obj.put("operationSource", "HuiJu_PLATFORM");
        obj.put("data", data);
        obj1.put("operationType", "INSERT");
        obj1.put("objectCode", "dxbm");
        obj1.put("objectCodeValue", "sbqg002");
        obj1.put("schema", "wltsfx_analysis");
        obj1.put("table", "nz_sb_jbsx");

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

            JSONArray btnArray = jsonObject.getJSONArray("datas");

            // 循环获取控件
            for (int i = 0; i < btnArray.size(); i++) {

                JSONObject kongjianObj = btnArray.getJSONObject(i);

                JSONObject json = kongjianObj.getJSONObject("json");// "json": {
                // }

                String controlName = "";
                if (json.containsKey("name")) {
                    controlName = json.getString("name");
                }
                JSONObject properties = null;
                if (json.containsKey("properties")) {
                    properties = JSON.parseObject(json.getString("properties"));
                    System.out.println(properties);
                    for (String key : properties.keySet()) {
                        JSONObject obj3 = new JSONObject();
                        obj3.put(key, properties.get(key));
                        columns.add(obj3);
                    }
                }

            }
            obj1.put("columns", columns);
            data.add(obj1);
            System.out.println(obj);
//			kafkaTemplate.send("test", obj);
        }
	}


	@RequestMapping("showPicture")
	public Picture showPicture(Long id){
		return pictureService.get(id);
	}


	@RequestMapping("handlePicture")
	public String handlePicture(Long id) {
	    String picJson="";
        Picture picture=showPicture(id);
        String jsonData = picture.getJson();
		JSONObject jsonObject = JSONObject.parseObject(jsonData);
		JSONObject pictureData = new JSONObject();
        pictureData.put("version","2.0");
        JSONArray datas=new JSONArray();
		JSONArray btnArray = jsonObject.getJSONArray("page1");

		//String datas = jsonObject.getString("datas");

		// 循环获取控件
		for (int i = 0; i < btnArray.size(); i++) {

			JSONObject kongjianObj = btnArray.getJSONObject(i);

			//name
			String name = "";
			if (kongjianObj.containsKey("name")) {
				name = kongjianObj.getString("name");
			}

			//text
			String text = "";
			if (kongjianObj.containsKey("text")) {
				text = kongjianObj.getString("text");
			}


            JSONObject obj = new JSONObject();
            JSONObject json = new JSONObject();



//            JSONObject jsonName = new JSONObject();


//			json.put("name", jsonName);


            if("通信链路".equals(name)){
          /*      JSONObject obj1 = new JSONObject();
                JSONObject json1 = new JSONObject();
                JSONObject jsonFrom = new JSONObject();
                JSONObject jsonTo = new JSONObject();
                JSONObject jsonFrom1 = new JSONObject();
                JSONObject jsonTo1 = new JSONObject();*/
                JSONObject styles = new JSONObject();
                JSONObject location = new JSONObject();
                JSONObject jsonLocation = new JSONObject();
                JSONObject path = new JSONObject();
                JSONObject jsonPath = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONArray points = new JSONArray();
                JSONObject jsonPath1 = new JSONObject();
                JSONArray points1 = new JSONArray();
                BigDecimal beginX=new BigDecimal(0);
                BigDecimal beginY=new BigDecimal(0);
                BigDecimal endX=new BigDecimal(0);
                BigDecimal endY=new BigDecimal(0);

                if (kongjianObj.containsKey("beginX")) {
                    beginX = kongjianObj.getBigDecimal("beginX").multiply(new BigDecimal(100));
                }

                if (kongjianObj.containsKey("beginY")) {
                    beginY = kongjianObj.getBigDecimal("beginY").multiply(new BigDecimal(100));
                }
                if (kongjianObj.containsKey("endX")) {
                    endX = kongjianObj.getBigDecimal("endX").multiply(new BigDecimal(100));
                }

                if (kongjianObj.containsKey("endY")) {
                    endY = kongjianObj.getBigDecimal("endY").multiply(new BigDecimal(100));
                }

                String connectId = "";
                if (kongjianObj.containsKey("connectId")) {
                    connectId = kongjianObj.getString("connectId");
                }
                String cnId[]= connectId.split(",");
         /*       obj.put("_className", "Q.Edge");
                obj.put("json", json);
                json.put("name","Edge");
                json.put("from",jsonFrom);
                jsonFrom.put("_ref",new BigDecimal(cnId[0]));
                json.put("to",jsonTo);
                jsonTo.put("_ref",new BigDecimal(cnId[1]));
                datas.add(obj);

                obj1.put("_className", "Q.Edge");
                obj1.put("json", json1);
                json1.put("name","Edge");
                json1.put("from",jsonFrom1);
                jsonFrom1.put("_ref",new BigDecimal(cnId[1]));
                json1.put("to",jsonTo1);
                jsonTo1.put("_ref",new BigDecimal(cnId[0]));
                datas.add(obj1);*/


                obj.put("_className", "Q.ShapeNode");
                obj.put("json", json);
                json.put("name","Line");
                json.put("styles",styles);
                styles.put("arrow.to","false");
                styles.put("layout.by.path","true");
                json.put("location",location);
                json.put("path",path);
                location.put("_className","Q.Point");
                location.put("json",jsonLocation);
                jsonLocation.put("x",beginX.add(endX).divide(new BigDecimal(2)));
                jsonLocation.put("y",beginY.add(endY).divide(new BigDecimal(2)));

                path.put("_className","Q.Path");
                path.put("json",jsonArray);
                jsonArray.add(jsonPath);
                jsonPath.put("type","m");
                jsonPath.put("points",points);
                points.add(0,beginX.add(endX).divide(new BigDecimal(2)).subtract(beginX));
                points.add(1,beginY.add(endY).divide(new BigDecimal(2)).subtract(beginY));

                jsonArray.add(jsonPath1);
                jsonPath1.put("type","1");
                jsonPath1.put("points",points1);
                points1.add(0,endX.subtract((beginX.add(endX).divide(new BigDecimal(2)))));
                points1.add(1, endY.subtract((beginY.add(endY).divide(new BigDecimal(2)))));
                datas.add(obj);

           /*     jsonFrom.put("_ref",cnId[1]);
                jsonTo.put("_ref",cnId[0]);
                datas.add(obj);*/
            }else{
                JSONObject location = new JSONObject();
                JSONObject jsonSize = new JSONObject();
                JSONObject sizeJson = new JSONObject();
                JSONObject jsonWidth = new JSONObject();
                JSONObject jsonHeight = new JSONObject();

//                List<Control> list =controlService.getControl(name);
//                String controlName=list.get(0).getName();

                //width
                String width = "";
                if (kongjianObj.containsKey("width")) {
                    width = kongjianObj.getString("width");
                }

                //height
                String height = "";
                if (kongjianObj.containsKey("height")) {
                    height = kongjianObj.getString("height");
                }

                BigDecimal x=new BigDecimal(0);
                BigDecimal y=new BigDecimal(0);

                if (kongjianObj.containsKey("pinX")) {
                      x = kongjianObj.getBigDecimal("pinX").multiply(new BigDecimal(100));
                }

                if (kongjianObj.containsKey("pinY")) {
                     y = kongjianObj.getBigDecimal("pinY").multiply(new BigDecimal(100));
                }

                String cid = "";
                if (kongjianObj.containsKey("id")) {
                    cid = kongjianObj.getString("id");
                }
                obj.put("_className","Q.Node");
                json.put("name","CS");
                json.put("location", location);
                location.put("x", x);
                location.put("y", y);
                jsonSize.put("size",sizeJson);
    			sizeJson.put("json",jsonWidth);
                sizeJson.put("json",jsonHeight);
                jsonWidth.put("width",width);
                jsonHeight.put("height",height);
                obj.put("json",json);
                obj.put("_refId",new BigDecimal(cid));
                datas.add(obj);
            }
		}

		pictureData.put("datas",datas);
        pictureData.put("scale",2);
        pictureData.put("tx",1000);
        pictureData.put("ty",1000);

        picJson=pictureData.toJSONString();
        picture.setJson(picJson);
        System.out.println(picJson);
		return picJson;
	}
}