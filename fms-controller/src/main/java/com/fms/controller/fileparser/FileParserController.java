package com.fms.controller.fileparser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.exception.FastDFSException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.domain.filemanage.BlockManage;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileParserExt;
import com.fms.domain.filemanage.FileType;
import com.fms.service.filemanage.*;
import com.fms.utils.*;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.StringUtil;
import com.handu.apollo.utils.exception.ApolloRuntimeException;
import com.handu.apollo.utils.json.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *  文件分析器管理
 * @author drc
 */
@RestController
@RequestMapping("/fileParser")
public class FileParserController {
    /**
     * 文件解析器service
     */
    @Autowired
    private FileParserService fileParserService;

	@Autowired
	private FileParserExtService fileParserExtService;

    @Autowired
    private FileService fileService;
    @Autowired
    private FastDFSTemplate fastDFSTemplate;
    /**
     * 文件分类service
     */
    @Autowired
    private FileTypeService fileTypeService;

	/**
	 * 黑、白名单管理service
	 */

	@Autowired
	private BlockManageService blockManageService;

    @Autowired
    private Environment env;

    /**
     * 查询文件解析器列表.
     * @param params
     * @return
     */
    @RequestMapping("getList")
    public Object getList(Map<String, Object> params) {
        return fileParserService.getList(params);
    }

    @RequestMapping("getOrderList")
    public Object getOrderList(Long id) {
        FileType ft = fileTypeService.getById(id);
        if (ft == null) {
            return fileParserService.getList(null);
        } else {
            return fileParserService.getOrderList(ft.getFileParserIds());
        }
    }

	/**
	 * 查询文件解析器列表.
	 * @param params
	 * @return
	 */
	@RequestMapping("getListForWaitClass")
	public Object getListForWaitClass(String fileSuffix , Map<String, Object> params) {

        List<FileParser> fileParserList = fileParserService.getList(params);

		Map<String, Object> paramsForfiletype = Maps.newHashMap();
        paramsForfiletype.put("fileSuffix", "<"+fileSuffix+">");
		List<FileType> fileTypeList =  fileTypeService.getListBySuffix(paramsForfiletype);
		Map<String,Object> fileParserIds = new HashMap<String,Object>();
		for(FileType ftype:fileTypeList){
			String fileParseId = ftype.getFileParserIds();
			if(!Strings.isNullOrEmpty(fileParseId)){
				String [] tFileParserIds = fileParseId.split(",");
				for(String tFP:tFileParserIds){
					if(!fileParserIds.containsKey(tFP)){
						fileParserIds.put(tFP,tFP);
					}
				}
			}
		}

        Set<String> filePK =  fileParserIds.keySet();
        if(filePK.size()>0) {
            Iterator<String> it = filePK.iterator();
            while(it.hasNext()){
                String recommendParserId=it.next();
                if(!Strings.isNullOrEmpty(recommendParserId)){
                    for (FileParser fileParser : fileParserList)
                    {
                        if (recommendParserId.equals(fileParser.getId().toString()))
                        {
                            fileParserList.remove(fileParser);
                            fileParserList.add(0,fileParser);
                            break;
                        }
                    }
                }
            }
        }
		return fileParserList;
	}


    /**
     * 分页查询解析器
     * @param params
     * @param request
     * @return
     */
    @RequestMapping("page")
    public Object page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Page page = ParamUtil.getPager(request);
        return fileParserService.page(params, page);
    }

	@RequestMapping("getParamList")
	public Object getParamList(String parserId)
	{
		Map<String, Object> params = new HashMap<String,Object>();

		params.put("parserId",parserId);

		return fileParserExtService.getList(params);
	}

    /**
     * 添加解析器
     * @param fileParser
     * @return
     */
    @RequestMapping("addParser")
    public Object addParser(@ModelAttribute FileParser fileParser) {
        Long parseId = fileParserService.add(fileParser);

        String parserExtstr = fileParser.getParserExt();

		JSONArray arrayList= JSONArray.parseArray(parserExtstr);
		//转换为目标对象list
		List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);

        //获取页面传过来的参数对象，循环设置解析器ID，保存入库
		//List<FileParserExt> parserExtList = fileParser.getParserExtList();


		if (CollectionUtil.isNotEmpty(parserExtList))
		{
			for (FileParserExt parserExt:parserExtList)
			{
				parserExt.setParserId(parseId);
				fileParserExtService.add(parserExt);
			}
		}

        return ExtUtil.success("添加成功！");
    }

    /**
     * 更新解析器
     * @param fileParser
     * @return
     */
    @RequestMapping("updateParser")
    public Object updateParser(@ModelAttribute FileParser fileParser) {
        fileParserService.update(fileParser);

        //更新操作执行时先把原来数据库数据删掉，后新增
		//获取页面传过来的参数对象，循环设置解析器ID，保存入库
		String parserExtstr = fileParser.getParserExt();

		JSONArray arrayList= JSONArray.parseArray(parserExtstr);
		//转换为目标对象list
		List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);

		fileParserExtService.delete(fileParser.getId());
		if (CollectionUtil.isNotEmpty(parserExtList))
		{
			for (FileParserExt parserExt:parserExtList)
			{
				parserExt.setParserId(fileParser.getId());
				fileParserExtService.add(parserExt);
			}
		}
        FileParser tmp = fileParserService.get(fileParser.getId());
        if (tmp != null && !fileParser.getSource().equals(tmp.getSource())) {
            Path path = Paths.get(tmp.getSource());
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ExtUtil.success("修改成功！");
    }

    /**
     * 删除解析器
     * @param id
     * @return
     */
    @RequestMapping("deleteParser")
    public Object deleteParser(Long id) {
        FileParser tmp = fileParserService.get(id);

        List<FileType> getFileTypes = fileTypeService.getListByFileParserId(id);

        if(getFileTypes!=null&&getFileTypes.size()>0){
            return ExtUtil.failure("该解析器正在被文件分类引用不能删除");
        }else{
            if (tmp != null && !Strings.isNullOrEmpty(tmp.getSource())) {
                Path path = Paths.get(tmp.getSource());
                if (Files.exists(path)) {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            fileParserService.delete(id);

			fileParserExtService.delete(id);
        }

        return ExtUtil.success("删除成功！");
    }

    /**
     * 上传解析器
     * @param file
     * @return
     */
    @RequestMapping("uploadParser")
    public Object uploadParser(MultipartFile file) {
        String parserPath = env.getProperty("parser.path");
        Path filePath = null;
        try {
            Path dir = Paths.get(parserPath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.createDirectories(dir);
            }

            String fileName = file.getOriginalFilename();
            filePath = Paths.get(parserPath + "/" + fileName);
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                Files.createFile(filePath);
            } else {
                return ExtUtil.failure("该解析器已经存在！");
            }
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ExtUtil.success(filePath.toString());
    }

    /**
     * 调用解析器方法
     * @param fileParser
     * @return
     */
    @RequestMapping("invokeMethod")
    public Object invokeMethod(@ModelAttribute FileParser fileParser) {
			Map<String, String> obj=this.readFileContent(fileParser);
				return obj.get("jsonBottomLevel");
    }

	/**
	 * 单文件解析并存入数据库
	 *
	 * @param fileParser
	 * @return
	 */
	@RequestMapping("parseDataSaveDatabase")
	public Object parseDataSaveDatabase(String jsonStr,String table_name,String customKeys,Long file_id,Long parserId){
		List<Map<String,Object>> data= JSONUtils.jsonToObject(jsonStr,List.class,Map.class);
		Map<String,String> customKey= JSONUtils.jsonToObject(customKeys,Map.class);
		//数据入库
		boolean result = fileParserService.parseDataSaveDatabase(data,table_name,customKey,file_id,parserId);
		if (result) {
			return ExtUtil.success("入库成功");
		} else {
			return ExtUtil.failure("入库失败");
		}
	}
	/**
	 * 读取文件内容
	 */
	private Map<String, String> readFileContent(FileParser fileParser){
		FileParser localParser = fileParserService.get(fileParser.getId());
		if (localParser == null) {
			throw new ApolloRuntimeException("该解析器不存在！");
		}
		try {
			com.fms.domain.filemanage.File local = fileService.get(Long.parseLong(fileParser.getParams()));
			byte[] buf = fastDFSTemplate.loadFile(local.getGroups(), local.getRealPath());
			String uriStr = "http://" + env.getProperty("fastdfs.nginxAddress") + ":" + env.getProperty("fastdfs.trackerHttpPort") + "/" + local.getGroups() + "/" + local.getRealPath();
			if (Strings.isNullOrEmpty(localParser.getSource())) {
				throw new ApolloRuntimeException("解析器路径为空！");
			}
			if (!Strings.isNullOrEmpty(local.getName())) {

			    if (local.getName().indexOf(".") > -1)
                {
                    local.setName(local.getName().substring(0, local.getName().lastIndexOf(".")));
                }
			    else
                {
                    local.setName(local.getName());
                }

			} else {
				local.setName(System.currentTimeMillis() + "");
			}
			if (!Strings.isNullOrEmpty(local.getType())) {
				local.setType("." + local.getType());
			} else {
				local.setType(".tmp");
			}
			String fileName = local.getName() + local.getType();
			File temp = FileUtil.createTempFile(fileName);
			FileUtils.writeByteArrayToFile(temp, buf);

			String parserExtstr = fileParser.getParserExt();

			JSONArray arrayList= JSONArray.parseArray(parserExtstr);
			//转换为目标对象list
			List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);


			Map<String, Object> extParams = new HashMap<>();

			BlockManage condition = new BlockManage();

			condition.setFileParserId(fileParser.getId());

			BlockManage block = blockManageService.get(condition);

			extParams.put("parserExtList",parserExtList);

			if (null != block)
			{
				extParams.put("blackContent",block.getBlackContent());

				extParams.put("whiteContent",block.getWhiteContent());
			}

			//解析文件内容
			return ParserUtil.parser(localParser, local, temp, extParams);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FastDFSException e) {
			e.printStackTrace();
			throw new ApolloRuntimeException("文件获取异常");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new ApolloRuntimeException(e.getMessage());
		}
		return null;
	}
	/**
	 * 单文件解析
	 *
	 * @param fileParser
	 * @return
	 */
	@RequestMapping("singleParse")
	public Object singleParse(@ModelAttribute FileParser fileParser){
		//解析文件内容
		Map<String, String> data = this.readFileContent(fileParser);
		if(data==null){
			return ExtUtil.failure("文件解析失败");
		}
		List<Map<String,Object>> json = new ArrayList<>();
		try{
			if(data.get("validateFileType")!=null&&data.get("validateFileType").equals("false")){
				return ExtUtil.failure("文件格式不匹配");
			}

			String jsonBottom = "[" + data.get("jsonBottomLevel") + "]";

			JSONArray arrayList= JSONArray.parseArray(jsonBottom);

			//获取返回数据中jsonBottomLevel每个tab对应的数据
			for(int i=0;i<arrayList.size();i++) {
				JSONObject jsonObject = arrayList.getJSONObject(i);

				Iterator<String> it = jsonObject.keySet().iterator();
				if (it.hasNext()){
					// 获得key
					String key = it.next();
					String value = jsonObject.getString(key);
					json.addAll(JSONUtils.jsonToObject(value,List.class,Map.class));

				}
			}
			//json=JSONUtils.jsonToObject(data.get("jsonBottomLevel"),List.class,Map.class);
		}catch (Exception e){
			return ExtUtil.failure(e.getMessage());
		}
		Map<String,Object> result=fileParserService.parseData(json);
		result.put("jsonStr",data.get("jsonBottomLevel"));
		return ExtUtil.success(result);
	}
	/**
	 * 批量解析 （待完善）
	 *
	 * @param params
	 * @param request
	 * @return
	 */
	@RequestMapping("multiParse")
	public Object multiParse(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		String preSel = ParamUtil.get(request, "selection", null);
		ObjectMapper mapper = JsonUtil.getMapper();
		List<com.fms.domain.filemanage.File> preFiles = null;
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			preFiles = mapper.readValue(preSel, new TypeReference<List<com.fms.domain.filemanage.File>>() {});
			List<com.fms.domain.filemanage.File> newFileList = new ArrayList<com.fms.domain.filemanage.File>();
			for (com.fms.domain.filemanage.File file: preFiles) {
				if ("待分类".equals(file.getClassType()) && null ==file.getRecommendParserId())
				{
					Map<String, Object> paramsForQuery = Maps.newHashMap();
					paramsForQuery.put("fileSuffix", "<"+file.getType()+">");
					List<FileType> fileTypeList =  fileTypeService.getListBySuffix(paramsForQuery);
					for(FileType ftype:fileTypeList){
						String fileParseId = ftype.getFileParserIds();
						if(!Strings.isNullOrEmpty(fileParseId)){
							String [] tFileParserIds = fileParseId.split(",");
							int count = 0;
							for(String tFP:tFileParserIds){
								if (StringUtils.isNotEmpty(tFP))
								{
									FileParser localParser = fileParserService.get(Long.valueOf(tFP));
									localParser.setParams(file.getId().toString());
									Map<String, String> data = this.readFileContent(localParser);
									if(data==null){
										return ExtUtil.failure("解析失败");
									}else if(data.get("validateFileType")!=null&&data.get("validateFileType").equals("false")){
										return ExtUtil.failure("文件格式不匹配");
									}
									if(data.get("jsonBottomLevel")==null){
										throw new ApolloRuntimeException("文件解析失败");
									}else{
										if (count >0)
										{
											com.fms.domain.filemanage.File cloneFile = (com.fms.domain.filemanage.File)file.clone();
											cloneFile.setParseResult(data.get("jsonBottomLevel"));
											newFileList.add(cloneFile);
										}
										else
										{
											file.setParseResult(data.get("jsonBottomLevel"));
										}
									}
									count++;
								}
							}
						}
					}
				}
				else
				{
					FileParser localParser = fileParserService.get(file.getRecommendParserId());
					localParser.setParams(file.getId().toString());
					Map<String, String> data = this.readFileContent(localParser);
					if(data==null){
						return ExtUtil.failure("解析失败");
					}else if(data.get("validateFileType")!=null&&data.get("validateFileType").equals("false")){
						return ExtUtil.failure("文件格式不匹配");
					}
					if(data.get("jsonBottomLevel")==null){
						throw new ApolloRuntimeException("文件解析失败");
					}else{
						file.setParseResult(data.get("jsonBottomLevel"));
					}
				}


			}

			preFiles.addAll(newFileList);
//            for (com.fms.domain.filemanage.File file: preFiles) {
//                byte[] buf = fastDFSTemplate.loadFile(file.getGroups(), file.getRealPath());
//                FileParser localParser = fileParserService.get(file.getRecommendParserId());
//                try {
//                    if (localParser == null) {
//                        System.out.println("该解析器不存在！");
//                        continue;
//                    }
//                    String uriStr = "http://" + env.getProperty("fastdfs.nginxAddress") + ":" + env.getProperty("fastdfs.trackerHttpPort") + "/" + file.getGroups() + "/" + file.getRealPath();
//                    if (Strings.isNullOrEmpty(localParser.getSource())) {
//                        System.out.println("解析器路径为空！");
//                        continue;
//                    }
//                    if (!Strings.isNullOrEmpty(file.getName())) {
//                        file.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
//                    } else {
//                        file.setName(System.currentTimeMillis() + "");
//                    }
//                    if (Strings.isNullOrEmpty(file.getType())) {
//                        file.setType("." + file.getType());
//                    } else {
//                        file.setType(".tmp");
//                    }
//                    String fileName = file.getName() + file.getType();
//                    File temp = FileUtil.createTempFile(fileName);
////                    FileUtils.copyURLToFile(new URL(uriStr), temp);
//                    FileUtils.writeByteArrayToFile(temp, buf);
//                    if (temp.exists()) {
//                        Object obj = ParserUtil.parser(localParser, file, temp, null);
//                        file.setParseResult(obj == null ? "" : obj.toString());
//                    } else {
//                        System.out.println("解析文件不存在！");
//                        continue;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return preFiles;
    }
    /**
     * 多文件解析入库
     */
    @RequestMapping("multiParseSaveData")
    public Object multiParseSaveData(String dataJSON,String parserDataJSON){
    	//解析器和文件信息
			List<Map<String,Long>> parserData=JSONUtils.jsonToObject(parserDataJSON,List.class,Map.class);
			//转换完毕后的解析数据
			List<List<Map<String, Object>>> finalParseData=new LinkedList<>();
			//多个解析结果拆分开
			String s[]=dataJSON.split("#");
			//解析结果从json转成对象
			for(String s1:s){
				List<Map<String, Object>> item= JSONUtils.jsonToObject(s1,List.class,Map.class);
				finalParseData.add(item);
			}
			//解析结果入库
			boolean result=fileParserService.multiParseSaveData(finalParseData,parserData);
			if (result) {
				return ExtUtil.success("入库成功");
			} else {
				return ExtUtil.failure("入库失败");
			}
	}

}
