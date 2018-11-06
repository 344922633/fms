package com.fms.service.filemanage;

import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.TableInfo;
import com.handu.apollo.base.Page;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.data.utils.Param;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 文件解析器服务实现类.
 *
 * @author drc
 */
@Service
@Transactional
public class FileParserService {
	public static final String CLASSNAME = FileParserService.class.getName() + CharPool.PERIOD;
	@Autowired
	private Dao dao;

	/**
	 * 查询文件解析器list
	 *
	 * @param params
	 * @return
	 */
	public List<FileParser> getList(Map<String, Object> params) {
		return dao.getList(CLASSNAME, "query", params);
	}

	public Page<FileParser> page(Map<String, Object> params, Page page) {
		return dao.page(CLASSNAME, "query", "queryCount", params, page);
	}

	public FileParser get(Long id) {
		return dao.get(CLASSNAME, "get", id);
	}

	public Long add(FileParser fileParser) {
		fileParser.setId(System.currentTimeMillis());
		dao.insert(CLASSNAME, "add", fileParser);

		return fileParser.getId();
	}

	public void update(FileParser fileParser) {
		dao.update(CLASSNAME, "update", fileParser);
	}

	public void delete(Long id) {
		dao.delete(CLASSNAME, "delete", id);
	}

	/**
	 * 根据id查询对应名称
	 *
	 * @param ids 解析器id集合
	 * @return
	 */
	public String getNamesByIds(String[] ids) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("ids", ids);
		return dao.get(CLASSNAME, "getNamesByIds", param);
	}

	public Object invoke(FileParser fileParser) {
		return null;
//        Object obj = JarLoadUtil.execute(fileParser.getSource(), fileParser.getClassName(), fileParser.getMethodName(), new Class[] {String.class}, new Object[] {"你好"});
	}

	public Object getOrderList(String ids) {
		Map<String, Object> params = Param.get()
						.put("ids", ids)
						.build();
		return dao.getList(CLASSNAME, "queryOrderList", params);
	}
	/**
	 * 解析数据并入库
	 */
	public boolean parseDataSaveDatabase(List<Map<String,Object>> data,String table_name,Map<String,String> customKey,Long file_id,Long parserId) {
		//构建入库数据
		Map<String,Object> sqlData=this.buildSqlData(data,table_name,customKey);
		//批量插入数据
		dao.insert(CLASSNAME,"parseDataSaveDatabase",sqlData);
		FileParser fileParser=new FileParser();
		fileParser.setId(parserId);
		fileParser=dao.get(FileParserService.CLASSNAME,"get",fileParser);
		//将文件修改为已解析状态
		File file=new File();
		file.setId(file_id);
		file.setIsParser(1);
		file.setRecommendParserName(fileParser.getName());
		file.setClassName(fileParser.getClassName());
		file.setRecommendParserId(parserId);
		dao.update(FileService.CLASSNAME,"update",file);
		return true;
	}
	/**
	 * 构建入库数据
	 */
	public Map<String,Object> buildSqlData(List<Map<String,Object>> data,String table_name,Map<String,String> customKey){
		//构建结果
		Map<String,Object> result=new LinkedHashMap<>();
		//json中所有的key
		Set<String> set=new HashSet<>();
		List<Map<String,Object>> tempList=new LinkedList<>();
		for (Map<String,Object> child : data) {
			Map<String,Object> temp=new LinkedHashMap<>();
			for (Map.Entry<String, Object> entry : child.entrySet()) {
				set.add(entry.getKey().toLowerCase());
				temp.put(entry.getKey().toLowerCase(),entry.getValue());
			}
			tempList.add(temp);
		}
		data=tempList;
		//数据库中存在的key
		Set<String> existFields=new LinkedHashSet<>();
		//查询所有表的字段信息
		List<TableInfo> data1=dao.getList(CLASSNAME,"queryTableInfo",null);
		//将表的字段信息以表名为key放到map中，方便处理
		Map<String,List<Map<String,String>>> tables =new LinkedHashMap<>();
		for(TableInfo tableInfo:data1){
			tables.put(tableInfo.getTable_name(),tableInfo.getColumnInfo());
		}
		//最大匹配的表的主键
		String pri_key=null;
		for(Map<String,String> table:tables.get(table_name)){
			//主键
			if("PRI".equals(table.get("column_key"))){
				pri_key=table.get("column_name");
			}
		}
		result.put("table_name",table_name);
		if(pri_key!=null){
			result.put("pri_key",pri_key);
			existFields.add(pri_key);
		}
		//用户自定义key加到匹配队列中
		for(Map.Entry<String,String> entry:customKey.entrySet()){
			existFields.add(entry.getValue());
			if(!entry.getKey().equals(entry.getValue())){
				for (Map<String,Object> child : data) {
					child.put(entry.getValue(),child.get(entry.getKey()));
				}
			}
		}
		result.put("columns",existFields);
		if(existFields.size()>0){
			if(pri_key!=null){
				for (Map<String,Object> child : data) {
					child.put(pri_key,System.currentTimeMillis()+new Random().nextInt(1000));
				}
			}
			result.put("data",data);
			return result;
		}
		return null;
	}

	public Map<String,Object> parseData(List<Map<String,Object>> data) {
		//构建结果
		Map<String,Object> result=new LinkedHashMap<>();
		//json中所有的key
		Set<String> set=new HashSet<>();
		for (Map<String,Object> child : data) {
			for (Map.Entry<String, Object> entry : child.entrySet()) {
				set.add(entry.getKey().toLowerCase());
			}
		}
		result.put("allKey",set);
		//查询所有表的字段信息
		List<TableInfo> data1=dao.getList(CLASSNAME,"queryTableInfo",null);
		//将表的字段信息以表名为key放到map中，方便处理
		Map<String,List<Map<String,String>>> tables =new LinkedHashMap<>();
		for(TableInfo tableInfo:data1){
			tables.put(tableInfo.getTable_name(),tableInfo.getColumnInfo());
		}
		//key的最大匹配次数
		int maxNum=0;
		//最大匹配的表名
		String maxTable=null;
		//匹配最多的表的已匹配字段
		Set<String> maxExistFields=null;
		//遍历所有表字段，找到匹配最多的表
		for(Map.Entry<String,List<Map<String,String>>> entry:tables.entrySet()){
			int num=0;
			Set<String> existFields=new LinkedHashSet<>();
			for(Map<String,String> table:entry.getValue()){
				String column_name=table.get("column_name").toLowerCase();
				for(String key:set){
					if(key.equals(column_name)){
						num++;
						existFields.add(column_name);
						break;
					}
				}
			}
			if(num>=maxNum){
				maxNum=num;
				maxTable=entry.getKey();
				maxExistFields=existFields;
			}
		}
		List<Map<String,String>> table=tables.get(maxTable);
		//构建每个字段的备选数据，已经匹配到的字段无法修改，为匹配到的字段可以选择其他未被使用的字段
		for(String key:set){
			Set<String> arrayList=new LinkedHashSet<>();
			//已经匹配到得字段，数据无法修改，只能取匹配到的列
			if(maxExistFields.contains(key)){
				arrayList.add(key);
				result.put(key,arrayList);
			}else{
				//为匹配的字段，可以从未使用的列中选择
				for(Map<String,String> column:table){
					String column_name=column.get("column_name").toLowerCase();
					for(String field:maxExistFields){
						if((!column.get("column_key").equals("PRI"))&&!maxExistFields.contains(column_name)&&!field.equals(column_name)){
							arrayList.add(column.get("column_name").toLowerCase());
						}
					}
				}
				result.put(key,arrayList);
			}
		}
		result.put("table_name",maxTable);
		return result;
	}
	/**
	 * 解析结果入库
	 */
	public boolean multiParseSaveData(List<List<Map<String,Object>>> finalParseData,List<Map<String,Long>> parserData) {
		int index=0;
		for(List<Map<String,Object>> item:finalParseData){
			Map<String,Object> parseData=this.multiParseData(item);
			//构建入库数据
			Map<String,Object> sqlData=this.buildSqlData(item,parseData.get("table_name").toString(), (Map<String, String>) parseData.get("customKey"));
			//批量插入数据
			dao.insert(CLASSNAME,"parseDataSaveDatabase",sqlData);
			Map<String,Long> parserItem=parserData.get(index);
			FileParser fileParser=new FileParser();
			fileParser.setId(parserItem.get("parserId"));
			fileParser=dao.get(FileParserService.CLASSNAME,"get",fileParser);
			//将文件修改为已解析状态
			File file=new File();
			file.setId(parserItem.get("fileId"));
			file.setIsParser(1);
			file.setRecommendParserName(fileParser.getName());
			file.setClassName(fileParser.getClassName());
			file.setRecommendParserId(parserItem.get("parserId"));
			dao.update(FileService.CLASSNAME,"update",file);
			index++;
		}
		return true;
	}
	public Map<String,Object> multiParseData(List<Map<String,Object>> data) {
		//构建结果
		Map<String,Object> result=new LinkedHashMap<>();
		//json中所有的key
		Set<String> set=new HashSet<>();
		for (Map<String,Object> child : data) {
			for (Map.Entry<String, Object> entry : child.entrySet()) {
				set.add(entry.getKey().toLowerCase());
			}
		}
		//查询所有表的字段信息
		List<TableInfo> data1=dao.getList(CLASSNAME,"queryTableInfo",null);
		//将表的字段信息以表名为key放到map中，方便处理
		Map<String,List<Map<String,String>>> tables =new LinkedHashMap<>();
		for(TableInfo tableInfo:data1){
			tables.put(tableInfo.getTable_name(),tableInfo.getColumnInfo());
		}
		//key的最大匹配次数
		int maxNum=0;
		//最大匹配的表名
		String maxTable=null;
		//字段映射信息
		Map<String,String> maxFieldMapInfo=null;
		//遍历所有表字段，找到匹配最多的表
		for(Map.Entry<String,List<Map<String,String>>> entry:tables.entrySet()){
			int num=0;
			Map<String,String> fieldMapInfo=new LinkedHashMap<>();
			for(Map<String,String> table:entry.getValue()){
				String column_name=table.get("column_name").toLowerCase();
				for(String key:set){
					if(key.equals(column_name)){
						num++;
						fieldMapInfo.put(column_name,column_name);
						break;
					}
				}
			}
			if(num>=maxNum){
				maxNum=num;
				maxTable=entry.getKey();
				maxFieldMapInfo=fieldMapInfo;
			}
		}
		result.put("customKey",maxFieldMapInfo);
		result.put("table_name",maxTable);
		return result;
	}
}
