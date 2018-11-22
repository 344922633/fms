package com.fms.controller.tuopu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fms.domain.tuopu.Picture;
import com.fms.service.tuopu.PicPropertyService;
import com.fms.service.tuopu.PictureService;
import com.fms.utils.ParamUtil;
import com.handu.apollo.base.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		if (jsonObject != null) {
			// 取出所有控件读取属性
			if (StringUtils.isEmpty(idStr)) {
				picture = new Picture();
				picture.setJson(jsonData);
				picture.setName(name);
				picture.setTime(date);
				pictureService.add(picture);
				JSONArray btnArray = jsonObject.getJSONArray("datas");
				//String datas = jsonObject.getString("datas");

				// 循环获取控件
				for (int i = 0; i < btnArray.size(); i++) {

					JSONObject kongjianObj = btnArray.getJSONObject(i);

					JSONObject json = kongjianObj.getJSONObject("json");// "json": {
					// }
					String location = "";
					if (json.containsKey("location")) {
						location = json.getString("location");
					}
					String controlId = "";
					if (json.containsKey("id")) {
						controlId = json.getString("id");
					}
					JSONObject properties = null;
					if (json.containsKey("properties")) {
						properties = json.getJSONObject("properties");

						for (String key : properties.keySet()) {
						/*	PicProperty pp = new PicProperty();
							pp.setPictureId(picture.getId());
							pp.setControlId(Long.parseLong(controlId));
							pp.setLocation(location);
							pp.setProperty(key);
							pp.setValue(properties.getString(key));*/
							JSONObject obj = new JSONObject();
							JSONObject obj1 = new JSONObject();
							JSONArray data = new JSONArray();
							JSONArray columns = new JSONArray();
							JSONObject obj2 = new JSONObject();
							JSONObject obj3 = new JSONObject();

							obj.put("operationSource", "HuiJu_PLATFORM");
							obj.put("data", data);
							obj1.put("operationType", "INSERT");
							obj1.put("objectCode", "dxbm");
							obj1.put("objectCodeValue", "sbqg002");
							obj1.put("schema", "wltsfx_analysis");
							obj1.put("table", "nz_sb_jbsx");
							obj1.put("columns", columns);
							data.add(obj1);
							columns.add(obj3);
							obj3.put(key, properties.get("key"));
							System.out.println(obj);
							kafkaTemplate.send("test", obj);

						}
					}
				}
			} else {
				picture = pictureService.get(id);
				picture.setJson(jsonData);
				picture.setName(name);
				picture.setTime(date);
				pictureService.update(picture);
				JSONArray btnArray = jsonObject.getJSONArray("datas");
				//String datas = jsonObject.getString("datas");

				// 循环获取控件
				for (int i = 0; i < btnArray.size(); i++) {

					JSONObject kongjianObj = btnArray.getJSONObject(i);

					JSONObject json = kongjianObj.getJSONObject("json");// "json": {
					// }
					String location = "";
					if (json.containsKey("location")) {
						location = json.getString("location");
					}
					String controlId = "";
					if (json.containsKey("id")) {
						controlId = json.getString("id");
					}
					JSONObject properties = null;
					if (json.containsKey("properties")) {
						properties = json.getJSONObject("properties");
						for (String key : properties.keySet()) {
						/*	PicProperty pp = new PicProperty();
							pp.setPictureId(picture.getId());
							pp.setControlId(Long.parseLong(controlId));
							pp.setLocation(location);
							pp.setProperty(key);
							pp.setValue(properties.getString(key));*/
							JSONObject obj = new JSONObject();
							JSONObject obj1 = new JSONObject();
							JSONArray data = new JSONArray();
							JSONArray columns = new JSONArray();
							JSONObject obj2 = new JSONObject();
							JSONObject obj3 = new JSONObject();

							obj.put("operationSource", "HuiJu_PLATFORM");
							obj.put("data", data);
							obj1.put("operationType", "UPDATE");
							obj1.put("objectCode", "dxbm");
							obj1.put("objectCodeValue", "sbqg002");
							obj1.put("schema", "wltsfx_analysis");
							obj1.put("table", "nz_sb_jbsx");
							obj1.put("columns", columns);
							data.add(obj1);
							columns.add(obj3);
							obj3.put(key, properties.get("key"));
							String str=obj.toJSONString();
							kafkaTemplate.send("test", str);

						}
					}
				}
			}
		}
	}

	@RequestMapping("showPicture")
	public Object showPicture(Long id){
		return pictureService.get(id);
	}

/*
	@RequestMapping("handlePicture")
	public Object handlePicture(HttpServletRequest request) {
		String jsonData = request.getParameter("json");
		JSONObject jsonObject = JSONObject.parseObject(jsonData);
		JSONObject pictureData = new JSONObject();
		JSONArray datas=new JSONArray();
		JSONArray btnArray = jsonObject.getJSONArray("datas");

		//String datas = jsonObject.getString("datas");

		// 循环获取控件
		for (int i = 0; i < btnArray.size(); i++) {

			JSONObject kongjianObj = btnArray.getJSONObject(i);


//			JSONObject json = kongjianObj.getJSONObject("json");
			// "location": {
			//          "x": 100,
			//          "y": 50
			//        }
			String pinX = "";
			String pinY = "";
			String x = "";
			String y = "";

			if (kongjianObj.containsKey("pinX")) {
				x = kongjianObj.getString("pinX");
			}

			if (kongjianObj.containsKey("pinY")) {
				y = kongjianObj.getString("pinY");
			}

			//name
			String name = "";
			if (kongjianObj.containsKey("name")) {
				name = kongjianObj.getString("name");
			}

			//weight
			String weight = "";
			if (kongjianObj.containsKey("weight")) {
				weight = kongjianObj.getString("weight");
			}

			//height
			String height = "";
			if (kongjianObj.containsKey("height")) {
				height = kongjianObj.getString("height");
			}

			//beginX
			String beginX = "";
			if (kongjianObj.containsKey("beginX")) {
				beginX = kongjianObj.getString("beginX");
			}

			//beginY
			String beginY = "";
			if (kongjianObj.containsKey("beginY")) {
				beginY = kongjianObj.getString("beginY");
			}

			//endX
			String endX = "";
			if (kongjianObj.containsKey("endX")) {
				beginY = kongjianObj.getString("endX");
			}

			//endY
			String endY = "";
			if (kongjianObj.containsKey("endY")) {
				endY = kongjianObj.getString("endY");
			}

			//text
			String text = "";
			if (kongjianObj.containsKey("text")) {
				text = kongjianObj.getString("text");
			}

			JSONObject obj = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject jsonName = new JSONObject();
			JSONObject location = new JSONObject();
			JSONObject jsonSize = new JSONObject();
			JSONObject sizeJson = new JSONObject();
			JSONObject jsonWidth = new JSONObject();
			JSONObject jsonHeight = new JSONObject();
			JSONObject jsonImage = new JSONObject();
			JSONObject _refId = new JSONObject();
			JSONObject jsonLocation = new JSONObject();
			JSONObject jsonFrom = new JSONObject();
			JSONObject jsonTo = new JSONObject();

			obj.put("json", json);
			json.put("name", jsonName);
			json.put("from", jsonFrom);
			json.put("to", jsonTo);
			json.put("location", location);
			location.put("x", x);
			location.put("y", y);
			jsonSize.put("json",sizeJson);
//			sizeJson.put("json",width);
			sizeJson.put("json",height);
			json.put("from",beginX);
//			json.put("to",_ref_to);
			datas.add(obj);
		}

		pictureData.put("version","2.0");
		pictureData.put("datas",datas);
*//*		pictureData.put("scale",scale);
		pictureData.put("tx",tx);
		pictureData.put("ty",ty);*//*
		return pictureData;
	}*/
}