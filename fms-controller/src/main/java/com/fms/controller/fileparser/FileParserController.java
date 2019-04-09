package com.fms.controller.fileparser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.domain.filemanage.*;
import com.fms.domain.schema.ColumnInfo;
import com.fms.domain.schema.TableInfo;
import com.fms.service.HdfsService;
import com.fms.service.ParserDefault.ParserDefaultService;
import com.fms.service.filemanage.*;
import com.fms.service.schema.ColumnInfoService;
import com.fms.service.schema.ColumnSetService;
import com.fms.utils.*;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.exception.ApolloRuntimeException;
import com.handu.apollo.utils.json.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ObjectUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件分析器管理
 *
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
    private HdfsService hdfsService;
    /**
     * 文件分类service
     */
    @Autowired
    private FileTypeService fileTypeService;
    @Autowired
    private ColumnSetService columnSetService;
    @Autowired
    private ColumnInfoService columnInfoService;

    /**
     * 黑、白名单管理service
     */

    @Autowired
    private BlockManageService blockManageService;

    @Autowired
    private Environment env;

    @Autowired
    private ParserDefaultService parserDefaultService;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 查询文件解析器列表.
     *
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
     *
     * @param params
     * @return
     */
    @RequestMapping("getListForWaitClass")
    public Object getListForWaitClass(String fileSuffix, Map<String, Object> params) {

        List<FileParser> fileParserList = fileParserService.getList(params);

        Map<String, Object> paramsForfiletype = Maps.newHashMap();
        paramsForfiletype.put("fileSuffix", "<" + fileSuffix + ">");
        List<FileType> fileTypeList = fileTypeService.getListBySuffix(paramsForfiletype);
        Map<String, Object> fileParserIds = new HashMap<String, Object>();
        for (FileType ftype : fileTypeList) {
            String fileParseId = ftype.getFileParserIds();
            if (!Strings.isNullOrEmpty(fileParseId)) {
                String[] tFileParserIds = fileParseId.split(",");
                for (String tFP : tFileParserIds) {
                    if (!fileParserIds.containsKey(tFP)) {
                        fileParserIds.put(tFP, tFP);
                    }
                }
            }
        }

        Set<String> filePK = fileParserIds.keySet();
        if (filePK.size() > 0) {
            Iterator<String> it = filePK.iterator();
            while (it.hasNext()) {
                String recommendParserId = it.next();
                if (!Strings.isNullOrEmpty(recommendParserId)) {
                    for (FileParser fileParser : fileParserList) {
                        if (recommendParserId.equals(fileParser.getId().toString())) {
                            fileParserList.remove(fileParser);
                            fileParserList.add(0, fileParser);
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
     *
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
    public Object getParamList(String parserId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parserId", parserId);
        List<FileParserExt> list = fileParserExtService.getList(params);
        if (!ObjectUtils.isEmpty(list)) {
            Map<String, Object> paramsForBlock = new HashMap<>();
            List<BlockManage> blocklist = blockManageService.getList(paramsForBlock);
            if (!ObjectUtils.isEmpty(blocklist)) {
                BlockManage block = blocklist.get(0);
                for (FileParserExt fileParserExt : list) {
                    if ("黑名单".equals(fileParserExt.getParameterName())) {
                        fileParserExt.setParameterValue(block.getBlackContent());
                    }
                    if ("白名单".equals(fileParserExt.getParameterName())) {
                        fileParserExt.setParameterValue(block.getWhiteContent());
                    }
                }
            }
        }

        return list;
    }

    /**
     * 添加解析器
     *
     * @param fileParser
     * @return
     */
    @RequestMapping("addParser")
    public Object addParser(@ModelAttribute FileParser fileParser) {
        Long parseId = fileParserService.add(fileParser);

        String parserExtstr = fileParser.getParserExt();

        JSONArray arrayList = JSONArray.parseArray(parserExtstr);
        //转换为目标对象list
        List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);

        //获取页面传过来的参数对象，循环设置解析器ID，保存入库
        //List<FileParserExt> parserExtList = fileParser.getParserExtList();

        String params = "";
        // 拼接params
        if (CollectionUtil.isNotEmpty(parserExtList)) {
            for (FileParserExt parserExt : parserExtList) {
                params += "," + parserExt.getParameterDesc();
                parserExt.setParserId(parseId);
                fileParserExtService.add(parserExt);
            }

            params = params.substring(1);
            System.out.println(params);
            fileParser.setParams(params);
            //更新params
            fileParserService.update(fileParser);
        }

        return ExtUtil.success("添加成功！");
    }

    /**
     * 更新解析器
     *
     * @param fileParser
     * @return
     */
    @RequestMapping("updateParser")
    public Object updateParser(@ModelAttribute FileParser fileParser) {
        fileParserService.update(fileParser);

        //更新操作执行时先把原来数据库数据删掉，后新增
        //获取页面传过来的参数对象，循环设置解析器ID，保存入库
        String parserExtstr = fileParser.getParserExt();

        JSONArray arrayList = JSONArray.parseArray(parserExtstr);
        //转换为目标对象list
        List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);

        fileParserExtService.delete(fileParser.getId());
        String params = "";

        if (CollectionUtil.isNotEmpty(parserExtList)) {
            for (FileParserExt parserExt : parserExtList) {
                params += "," + parserExt.getParameterDesc();
                parserExt.setParserId(fileParser.getId());
                fileParserExtService.add(parserExt);
            }
            params = params.substring(1);
            fileParser.setParams(params);
            fileParserService.update(fileParser);

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
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteParser")
    public Object deleteParser(Long id) {
        FileParser tmp = fileParserService.get(id);

        List<FileType> getFileTypes = fileTypeService.getListByFileParserId(id);

        if (getFileTypes != null && getFileTypes.size() > 0) {
            return ExtUtil.failure("该解析器正在被文件分类引用不能删除");
        } else {
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
     *
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
     *
     * @param fileParser
     * @return
     */
    @RequestMapping("invokeMethod")
    public Object invokeMethod(@ModelAttribute FileParser fileParser) {
        Map<String, String> obj = this.readFileContent(fileParser);
        return obj.get("jsonBottomLevel");
    }


    /**
     * 单文件解析并存入数据库
     *
     * @param jsonStr
     * @param table_name
     * @param customKeys
     * @param file_id
     * @param parserId
     * @return
     */
    @RequestMapping("parseDataSaveDatabase")
    public Object parseDataSaveDatabase(String jsonStr, String table_name, String customKeys, Long file_id, Long parserId) {
        List<Map<String, Object>> data = JSONUtils.jsonToObject(jsonStr, List.class, Map.class);
        Map<String, String> customKey = JSONUtils.jsonToObject(customKeys, Map.class);
        //数据入库
        boolean result = fileParserService.parseDataSaveDatabase(data, table_name, customKey, file_id, parserId);
        if (result) {
            return ExtUtil.success("入库成功");
        } else {
            return ExtUtil.failure("入库失败");
        }
    }

    /**
     * 读取文件内容
     */
    private Map<String, String> readFileContent(FileParser fileParser) {
        FileParser localParser = fileParserService.get(fileParser.getId());
        if (localParser == null) {
            throw new ApolloRuntimeException("该解析器不存在！");
        }
        try {
            com.fms.domain.filemanage.File local = fileService.get(Long.parseLong(fileParser.getParams()));
            byte[] buf = hdfsService.cat(local.getRealPath());
//            String uriStr = "http://" + env.getProperty("fastdfs.nginxAddress") + ":" + env.getProperty("fastdfs.trackerHttpPort") + "/" + local.getGroups() + "/" + local.getRealPath();
            if (Strings.isNullOrEmpty(localParser.getSource())) {
                throw new ApolloRuntimeException("解析器路径为空！");
            }
            if (!Strings.isNullOrEmpty(local.getName())) {

                if (local.getName().indexOf(".") > -1) {
                    local.setName(local.getName().substring(0, local.getName().lastIndexOf(".")));
                } else {
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

            Map<String, Object> extParams = new HashMap<>();

            String parserExtstr = fileParser.getParserExt();
            JSONArray arrayList = JSONArray.parseArray(parserExtstr);
            if (arrayList != null) {
                //转换为目标对象list
                List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);

                extParams.put("parserExtList", parserExtList);
            } else {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("parserId", fileParser.getId());

                List<FileParserExt> parserExtList = fileParserExtService.getList(params);
                if (!ObjectUtils.isEmpty(parserExtList)) {
                    extParams.put("parserExtList", parserExtList);
                }
            }
            BlockManage condition = new BlockManage();

            condition.setFileParserId(fileParser.getId());

            BlockManage block = blockManageService.get(condition);


            if (null != block) {
                extParams.put("blackContent", block.getBlackContent());

                extParams.put("whiteContent", block.getWhiteContent());
            }

            //解析文件内容
            return ParserUtil.parser(localParser, local, temp, extParams);
        } catch (IOException e) {
            e.printStackTrace();
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
    public Object singleParse(@ModelAttribute FileParser fileParser) {
        //解析文件内容
        Map<String, String> data = this.readFileContent(fileParser);
        if (data == null) {
            return ExtUtil.failure("文件解析失败");
        }
        List<Map<String, Object>> json = new ArrayList<>();
        //json中所有的key
        Set<String> set = new HashSet<>();
        try {
            if (data.get("validateFileType") != null && data.get("validateFileType").equals("false")) {
                return ExtUtil.failure("文件格式不匹配");
            }
            String jsonBottom = data.get("jsonBottomLevel");
            JSONObject jb = JSON.parseObject(jsonBottom);
            jsonBottom = "[" + data.get("jsonBottomLevel") + "]";
            JSONArray arrayList = JSONArray.parseArray(jsonBottom);

            for (String key : jb.keySet()) {
                JSONArray keyArray = jb.getJSONArray(key);
                for (int i = 0; i < keyArray.size(); i++) {
                    for (String valueKey : keyArray.getJSONObject(i).keySet()) {
                        set.add(valueKey);
                    }
                }
            }
            for (int j = 0; j < arrayList.size(); j++) {
                JSONObject jsonObject = arrayList.getJSONObject(j);

                Iterator<String> it = jsonObject.keySet().iterator();
                if (it.hasNext()) {
                    // 获得key
                    String key = it.next();
                    String value = jsonObject.getString(key);
                    json.addAll(JSONUtils.jsonToObject(value, List.class, Map.class));
                }
            }


            Map<String, Object> result = fileParserService.parseData(json);
            isParser(fileParser);
            result.put("allKeyForDisplay", set);
            result.put("jsonStr", data.get("jsonBottomLevel"));
            return ExtUtil.success(result);
        } catch (Exception e) {
            return ExtUtil.failure(e.getMessage());
        }

    }

    //将文件修改为已解析状态

    private void isParser(FileParser fileParser) {
        Long id = Long.parseLong(fileParser.getParams());
        fileParserService.updateIsParser(id, 1);
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
            preFiles = mapper.readValue(preSel, new TypeReference<List<com.fms.domain.filemanage.File>>() {
            });
            List<com.fms.domain.filemanage.File> newFileList = new ArrayList<com.fms.domain.filemanage.File>();
            for (com.fms.domain.filemanage.File file : preFiles) {
                if (null == file.getRecommendParserId()) {
                    if ("待分类".equals(file.getClassType())) {
                        Map<String, Object> paramsForQuery = Maps.newHashMap();
                        paramsForQuery.put("fileSuffix", "<" + file.getType() + ">");
                        List<FileType> fileTypeList = fileTypeService.getListBySuffix(paramsForQuery);
                        for (FileType ftype : fileTypeList) {
                            String fileParseId = ftype.getFileParserIds();
                            if (!Strings.isNullOrEmpty(fileParseId)) {
                                String[] tFileParserIds = fileParseId.split(",");
                                int count = 0;
                                for (String tFP : tFileParserIds) {
                                    if (StringUtils.isNotEmpty(tFP)) {
                                        try {
                                            List<FileParserExt> pext = new ArrayList<>();
                                            JSONObject obj = JSONObject.parseObject(preSel);
                                            JSONArray array = obj.getJSONArray("paramList");
                                            for (int i = 0; i < array.size(); i++) {
                                                FileParserExt fpext = new FileParserExt();
                                                JSONObject obj1 = array.getJSONObject(i);
                                                String parameterDesc = obj1.getString("parameterDesc");
                                                String parameterName = obj1.getString("parameterName");
                                                String parameterValue = obj1.getString("parameterValue");
                                                fpext.setParameterDesc(parameterDesc);
                                                fpext.setParameterName(parameterName);
                                                fpext.setParameterValue(parameterValue);
                                                pext.add(fpext);
                                            }

                                            FileParser localParser = fileParserService.get(Long.valueOf(tFP));
                                            localParser.setParserExt(pext.toString());
                                            localParser.setParams(file.getId().toString());
                                            Map<String, String> data = this.readFileContent(localParser);
                                            if (data == null) {
                                                continue;
                                                //return ExtUtil.failure("解析失败");
                                            } else if (data.get("validateFileType") != null && data.get("validateFileType").equals("false")) {
                                                continue;
                                                //return ExtUtil.failure("文件格式不匹配");
                                            }
                                            if (data.get("jsonBottomLevel") == null) {
                                                continue;
                                                //throw new ApolloRuntimeException("文件解析失败");
                                            } else {
                                                if (count > 0) {
                                                    com.fms.domain.filemanage.File cloneFile = (com.fms.domain.filemanage.File) file.clone();
                                                    cloneFile.setRecommendParserId(Long.valueOf(tFP));
                                                    cloneFile.setParseResult(data.get("jsonBottomLevel"));
                                                    newFileList.add(cloneFile);
                                                } else {
                                                    file.setRecommendParserId(Long.valueOf(tFP));
                                                    file.setParseResult(data.get("jsonBottomLevel"));
                                                }
                                            }
                                            count++;
                                        } catch (Exception e) {
                                            continue;
                                        }
                                    }
                                }

                                if (count == 0) {
                                    return ExtUtil.failure("解析失败");
                                }
                            }
                        }
                    }
                    if ("其他".equals(file.getClassType())) {
                        String user = ParamUtil.get(request, "user", null);
                        ParserDefaultDo parserDefaultDo = parserDefaultService.getByName(user);
                        String fileParseId = parserDefaultDo.getFileParserIds();
                        if (!Strings.isNullOrEmpty(fileParseId)) {
                            String[] tFileParserIds = fileParseId.split(",");
                            int count = 0;
                            for (String tFP : tFileParserIds) {
                                if (StringUtils.isNotEmpty(tFP)) {
                                    try {
                                        List<FileParserExt> pext = new ArrayList<>();
                                        JSONObject obj = JSONObject.parseObject(preSel);
                                        JSONArray array = obj.getJSONArray("paramList");
                                        for (int i = 0; i < array.size(); i++) {
                                            FileParserExt fpext = new FileParserExt();
                                            JSONObject obj1 = array.getJSONObject(i);
                                            String parameterDesc = obj1.getString("parameterDesc");
                                            String parameterName = obj1.getString("parameterName");
                                            String parameterValue = obj1.getString("parameterValue");
                                            fpext.setParameterDesc(parameterDesc);
                                            fpext.setParameterName(parameterName);
                                            fpext.setParameterValue(parameterValue);
                                            pext.add(fpext);
                                        }

                                        FileParser localParser = fileParserService.get(Long.valueOf(tFP));
                                        localParser.setParams(file.getId().toString());
                                        localParser.setParserExt(pext.toString());
                                        Map<String, String> data = this.readFileContent(localParser);
                                        if (data == null) {
                                            continue;
                                            //return ExtUtil.failure("解析失败");
                                        } else if (data.get("validateFileType") != null && data.get("validateFileType").equals("false")) {
                                            continue;
                                            //return ExtUtil.failure("文件格式不匹配");
                                        }
                                        if (data.get("jsonBottomLevel") == null) {
                                            continue;
                                            //throw new ApolloRuntimeException("文件解析失败");
                                        } else {
                                            if (count > 0) {
                                                com.fms.domain.filemanage.File cloneFile = (com.fms.domain.filemanage.File) file.clone();
                                                cloneFile.setRecommendParserId(Long.valueOf(tFP));
                                                cloneFile.setParseResult(data.get("jsonBottomLevel"));
                                                newFileList.add(cloneFile);
                                            } else {
                                                file.setRecommendParserId(Long.valueOf(tFP));
                                                file.setParseResult(data.get("jsonBottomLevel"));
                                            }
                                        }
                                        count++;
                                    } catch (Exception e) {
                                        continue;
                                    }
                                }
                            }
                            if (count == 0) {
                                return ExtUtil.failure("解析失败");
                            }

                        }
                    }
                } else {
                    FileParser localParser = fileParserService.get(file.getRecommendParserId());
                    localParser.setParams(file.getId().toString());
                    Map<String, String> data = this.readFileContent(localParser);
                    if (data == null) {
                        return ExtUtil.failure("解析失败");
                    } else if (data.get("validateFileType") != null && data.get("validateFileType").equals("false")) {
                        return ExtUtil.failure("文件格式不匹配");
                    }
                    if (data.get("jsonBottomLevel") == null) {
                        throw new ApolloRuntimeException("文件解析失败");
                    } else {
                        file.setParseResult(data.get("jsonBottomLevel"));
                        //将文件修改为已解析状态
                        String name = file.getName();
                        fileParserService.updateIsParserMultiFile(name, 1);

                    }
                }
            }
            preFiles.addAll(newFileList);
        } catch (Exception e) {
            return ExtUtil.failure(e.getMessage());
        }
        return preFiles;
    }

    /**
     * 多文件解析入库
     */

    @RequestMapping("multiParseSaveData")
    public Object multiParseSaveData(String dataJSON, String parserDataJSON) {
        //解析器和文件信息
        List<Map<String, Long>> parserData = JSONUtils.jsonToObject(parserDataJSON, List.class, Map.class);
        //转换完毕后的解析数据
        List<List<Map<String, Object>>> finalParseData = new LinkedList<>();
        //多个解析结果拆分开
        String s[] = dataJSON.split("#");
        //解析结果从json转成对象
        for (String s1 : s) {
            List<Map<String, Object>> item = JSONUtils.jsonToObject(s1, List.class, Map.class);
            finalParseData.add(item);
        }
        //解析结果入库
        boolean result = fileParserService.multiParseSaveData(finalParseData, parserData);
        if (result) {
            return ExtUtil.success("入库成功");
        } else {
            return ExtUtil.failure("入库失败");
        }
    }

    @RequestMapping("getDefaultParser")
    public Object getDefaultParser(String user) {
        return parserDefaultService.getByName(user);
    }

    @RequestMapping("updateDefaultParser")
    public Object updateDefaultParser(@ModelAttribute ParserDefaultDo parserDefaultDo) {
        parserDefaultService.delete(parserDefaultDo.getUser());
        parserDefaultService.add(parserDefaultDo);
        return ExtUtil.success("修改成功！");
    }

    /**
     * @param json
     * @param data
     * @return
     */
    private Object singleFileStrFormatNew(JSONObject json, JSONObject data) {
        boolean result = false;
        System.out.println(json);
        //下面这句话不知道干什么的    JSONObject 与 JSONArray之间转换各种报错。
        JSONObject jsonNew = new JSONObject();
        for (String key : json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            JSONArray arrayNew = new JSONArray();

            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                JSONObject objNew = new JSONObject();
                for (String key1 : obj.keySet()) {
                    objNew.put(key1.toLowerCase(), obj.get(key1));
                }
                arrayNew.add(objNew);
            }
            jsonNew.put(key, arrayNew);
        }
        System.out.println(jsonNew);

        // 获取日志key
        String logKeyStr = "";
        for (String logKey : json.keySet()) {
            String id = null;
            logKeyStr = logKey;
            // 日志数组，遍历获取kafka消息
            JSONArray logArray = json.getJSONArray(logKeyStr);

            Map<String, Set<String>> map = new HashMap<String, Set<String>>();
            Map<String, String> valueMap = new HashMap<String, String>();
            Map<String, JSONObject> dicMap = new HashMap<String, JSONObject>();

            for (String key : data.keySet()) {
                JSONObject colJson = data.getJSONObject(key);
                //当schemeId为空或者0时不发 kafka
                // columnId 为空，不发kafka
                //columnId1为前台传值问题，其实是columnId修改后的值，不知道为什么要这么传
                String columnId = colJson.getString("columnId");
                String schemaId = colJson.getString("schemaId");
                String columnId1 = colJson.getString("columnId+''");
                if(columnId1!=null){
                    columnId=columnId1;
                }
                id = columnId;

                if (StringUtils.isNotEmpty(schemaId) && !(schemaId.equals("0"))) {
                    if (StringUtils.isNotEmpty(columnId)) {
                        String table_schema = colJson.getString("schemaId") + "_" + colJson.getString("tableId");

                        Set<String> column = null;
                        if (map.containsKey(table_schema)) {
                            column = map.get(table_schema);
                        } else {
                            column = new HashSet<String>();
                        }
                        column.add(columnId);
                        map.put(table_schema, column);
                        //columnId2 与 columnID1 是一个道理
                        String columnId2 = colJson.getString("columnId+''");
                        if(columnId2!=null){
                            columnId=columnId1;
                        }
                        id = columnId;
//                    String valueKey = table_schema + "_" + colJson.getString("columnId");
                        String valueKey = table_schema + "_" + columnId;
                        valueMap.put(valueKey, key.toLowerCase());
                        dicMap.put(table_schema, colJson.getJSONObject("dicMap"));
                    }
                }
            }

            for (int i = 0; i < logArray.size(); i++) {
                JSONObject appacheLog = logArray.getJSONObject(i);
                String objectCodeValue = "dwj_" + System.nanoTime();

                JSONObject rootObj = new JSONObject();
                rootObj.put("operationSource", PropertyUtil.readValue("OPERATION_SOURCE"));

                JSONArray infoArr = new JSONArray();
                for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                    JSONObject infoObj = new JSONObject();
                    infoObj.put("operationType", "INSERT");
                    infoObj.put("objectCode", "dxbm");
                    infoObj.put("objectCodeValue", objectCodeValue);
                    //库名
                    infoObj.put("schema", env.getProperty("schema"));
                    JSONObject columnPublic = new JSONObject();
                    JSONArray columnArr = new JSONArray();
                    // schemaId_tableId
                    String mapKey = entry.getKey();
                    // dicMap "dicMap": { "fghj": "1", "fghk": "2"  }
                    JSONObject dicJSON = dicMap.get(mapKey);

                    String schemaId = mapKey.split("_")[0];
                    String tableId = mapKey.split("_")[1];

                    TableInfo tableInfo = columnSetService.getTableNameByTableId(Long.parseLong(tableId));
                    //表名
                    infoObj.put("table", tableInfo.getTableEnglish());

                    Set<String> colSet = entry.getValue();
                    for (String colId : colSet) {
                        JSONObject columnJson = new JSONObject();
                        // 75 29 10
                        ColumnInfo columnInfo = columnSetService.getColumnInfo(Integer.valueOf(colId));
                        // 获取column名称  ：非枚举字段
                        columnJson.put("name", columnInfo.getColumnEnglish().toLowerCase());
                        // schemaId_tableId_columnId
                        String valueKey = mapKey + "_" + colId;
                        // bytes
                        String logKey1 = valueMap.get(valueKey);
                        columnJson.put("value", appacheLog.get(logKey1.toLowerCase()));
                        if (appacheLog.get(logKey1.toLowerCase()) != null) {
                            columnArr.add(columnJson);
                        }
                    }

                    if (dicJSON != null) {
                        boolean trans = false;
                        String key1 = null;
                        String value1=null;
                        for (String dicKey : dicJSON.keySet()) {
                            JSONObject columnJson = new JSONObject();
                            // 获取枚举字段
                            if (StringUtils.isBlank(dicJSON.getString(dicKey))) {
                                key1 =dicKey.toLowerCase();
                            }else{
                                value1 = dicJSON.getString(dicKey);
                            }
                            if(key1!=null&&value1!=null){
                                columnJson.put("name", key1);
                                columnJson.put("value", value1);
                                columnArr.add(columnJson);
                            }
                        }
                    }

                    columnPublic.put("name", "dxbm");
                    columnPublic.put("value", objectCodeValue);
                    columnArr.add(columnPublic);

                    infoObj.put("columns", columnArr);

                    infoArr.add(infoObj);

                    rootObj.put("data", infoArr);
                }

                System.out.println("kafka消息格式：\n" + rootObj);


                kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"), rootObj.toJSONString());
                result = true;
            }
        }
        return result;
    }

    @RequestMapping("parseDataSaveHBase")
    public Object parseDataSaveHBase(String jsonStr, String columnKeyNamesMap, String customKeys, Long file_id, Long parserId) {

        JSONObject json = JSONObject.parseObject(jsonStr);
        JSONObject customKey = JSONObject.parseObject(customKeys);
        // 循环获取tableName 发送kafka
        JSONObject data = JSONObject.parseObject(columnKeyNamesMap);
//         新的kafka数据格式
        singleFileStrFormatNew(json, data);

        com.fms.domain.filemanage.File file = fileService.get(file_id);
        String fileMD5 = file.getFileMd5();
        String fileName = file.getName();
        String fileInfo = file.toString();
        List<FileType> typeList = fileTypeService.getListByFileParserId(parserId);
        String fileType = null;
        if(typeList.size() != 0){
            fileType = typeList.get(0).getType();
        }

        //数据入库
        boolean result = fileParserService.parseDataSaveDataHBase(file_id, parserId, jsonStr, fileInfo, fileType, fileMD5, fileName);

        if (result) {
            return ExtUtil.success("入库成功");
        } else {
            return ExtUtil.failure("入库失败");
        }
    }


    /**
     * 多文件解析入HBase库
     */
    @RequestMapping("multiParseSaveDataToHBase")
    public Object multiParseSaveDataToHBase(String dataJSON, String parserDataJSON, String dataIdJSON, String columnKeyNamesMapObj) {

//        //转换完毕后的解析数据
//         List<List<Map<String, Object>>> finalParseData = new LinkedList<>();
        //解析器和文件信息
        List<Map<String, Long>> parserData = JSONUtils.jsonToObject(parserDataJSON, List.class, Map.class);
        //多个解析结果拆分开
        String s[] = dataJSON.split("#");
        String id[] = dataIdJSON.split("#");

        for (int i = 0; i < s.length; i++) {
            if (i % 2 == 0) {
                com.fms.domain.filemanage.File file = fileService.get(parserData.get(i).get("fileId"));
                String fileMD5 = file.getFileMd5();
                String fileName = file.getName();
                String fileInfo = file.toString();
                Long parserId = file.getRecommendParserId();
                Long file_id  = file.getId();

                List<FileType> typeList = fileTypeService.getListByFileParserId(parserData.get(i).get("parserId"));
                String fileType = typeList.get(0).getType();
                //数据入库;
                String jsonStr = s[i];
                fileParserService.multiParseDataSaveDataHBase(file_id, parserId, jsonStr, fileInfo, fileType, fileMD5, fileName);
            }
        }

        JSONObject jsonTemp = JSONObject.parseObject(columnKeyNamesMapObj);
        Iterator<String> it = jsonTemp.keySet().iterator();
        while(it.hasNext()){
            // 获得key
            String key = it.next();
            JSONObject data = jsonTemp.getJSONObject(key);
            JSONObject json = jsonTemp.getJSONObject(key);
            singleFileStrFormat(json, data);
        }
        return ExtUtil.failure("执行完毕");

    }

    //多文件
    private void singleFileStrFormat(JSONObject json, JSONObject data) {
        for (String key : json.keySet()) {
            JSONObject colJson = json.getJSONObject(key);

            Map<String, Set<String>> map = new HashMap<>();
            Map<String, String> valueMap = new HashMap<>();
            Map<String, JSONObject> dicMap = new HashMap<>();
            String columnId = colJson.getString("columnId");
            String schemaId = colJson.getString("schemaId");
            String columnId1 = null;
            if(colJson.containsKey("columnId+''")){
                columnId1 = colJson.get("columnId+''").toString();
            }
            if(columnId1!=null){
                columnId=columnId1;
            }

            if (StringUtils.isNotEmpty(schemaId) && !(schemaId.equals("0"))) {
                if (StringUtils.isNotEmpty(columnId)) {
                    String table_schema = colJson.getString("schemaId") + "_" + colJson.getString("tableId");

                    Set<String> column = null;
                    if (map.containsKey(table_schema)) {
                        column = map.get(table_schema);
                    } else {
                        column = new HashSet<String>();
                    }
                    column.add(columnId);
                    map.put(table_schema, column);
                    String columnId2 = colJson.getString("columnId+''");
                    if(columnId2!=null){
                        columnId=columnId1;
                    }
                    String valueKey = table_schema + "_" + columnId;
                    valueMap.put(valueKey, key.toLowerCase());
                    dicMap.put(table_schema, colJson.getJSONObject("dicMap"));
                }
            }
            for (String key2 : colJson.keySet()) {
                String objectCodeValue = "dwj_" + System.nanoTime();
                JSONObject rootObj = new JSONObject();
                rootObj.put("operationSource", PropertyUtil.readValue("OPERATION_SOURCE"));
                JSONArray infoArr = new JSONArray();
                for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                    JSONObject infoObj = new JSONObject();
                    infoObj.put("operationType", "INSERT");
                    infoObj.put("objectCode", "dxbm");
                    infoObj.put("objectCodeValue", objectCodeValue);
                    //库名
                    infoObj.put("schema", env.getProperty("schema"));
                    JSONObject columnPublic = new JSONObject();
                    JSONArray columnArr = new JSONArray();
                    String mapKey = entry.getKey();
                    // dicMap "dicMap": { "fghj": "1", "fghk": "2"  }
                    JSONObject dicJSON = dicMap.get(mapKey);
                    String tableId = mapKey.split("_")[1];
                    TableInfo tableInfo = columnSetService.getTableNameByTableId(Long.parseLong(tableId));
                    //表名
                    infoObj.put("table", tableInfo.getTableEnglish());
                    Set<String> colSet = entry.getValue();
                    for (String colId : colSet) {
                        JSONObject columnJson = new JSONObject();
                        // 75 29 10
                        ColumnInfo columnInfo = columnSetService.getColumnInfo(Integer.valueOf(colId));
                        // 获取column名称  ：非枚举字段
                        columnJson.put("name", columnInfo.getColumnEnglish().toLowerCase());
                    }

                    if (dicJSON != null) {
                        int i = 0;
                        String value ;
                        String name ;
                        String value1 = null;
                        String name1 = null;
                        for (String dicKey : dicJSON.keySet()) {
                            i++;
                            JSONObject columnJson = new JSONObject();
                            // 获取枚举字段
                            if (StringUtils.isNotEmpty(dicJSON.getString(dicKey))) {
                                value1 = dicJSON.getString(dicKey);
                            }else{
                                name1 = dicKey.toLowerCase();
                            }
                            if(name1 != null&& value1 !=null){
                                value = value1;
                                name = name1;
                                if (i % 2 == 0) {
                                    columnJson.put("name", name);
                                    columnJson.put("value", value);
                                    columnArr.add(columnJson);
                                }
                            }


                        }
                    }
                    columnPublic.put("name", "dxbm");
                    columnPublic.put("value", objectCodeValue);
                    columnArr.add(columnPublic);
                    infoObj.put("columns", columnArr);
                    infoArr.add(infoObj);
                    rootObj.put("data", infoArr);
                }
                if(rootObj.containsKey("data")){
                    System.out.println("kafka消息格式：\n" + rootObj);
                    kafkaTemplate.send(PropertyUtil.readValue("DEFAULT_TOPIC"), rootObj.toJSONString());
                    break;
                }
            }
            break;
        }
    }
}
