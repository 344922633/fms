package com.fms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileParserExt;
import com.fms.utils.FileUtil;
import com.fms.utils.ParserUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ObjectUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件解析测试
 *
 * @author Brian
 */
public class Test {
    public static void main(String[] args) {
        /*System.out.println("124.txt".substring("123.txt".lastIndexOf(".")));
        String[] a = new String[]{"a", "b", "c", "d"};
        String[] b = new String[a.length - 1];
        System.arraycopy(a, 1, b, 0 , b.length);
        System.out.println(b.toString());
        File f = new File("/Users/fencho/Downloads/cn_windows_10_multiple_editions_version_1607_updated_jul_2016_x64_dvd_9056935.iso");
        System.out.println(f.getName());*/

//        解析器信息
        FileParser localParser = new FileParser();

        localParser.setId(1539050371244L);
        localParser.setName("Ppt解析器");
        localParser.setSource("E:\\unstructuredTextParser.jar");
        localParser.setClassName("com.caeit.parser.ppt.PptParser");
        localParser.setParams("黑名单,白名单");
        localParser.setMethodName("parsePpt");

//        文件信息
        com.fms.domain.filemanage.File local = new com.fms.domain.filemanage.File();

        local.setId(1548141683207L);
        local.setName("PptParser_testFile.pptx");
        local.setRealPath("E:\\PptParser_testFile.pptx");
        local.setType("pptx");
        local.setDirectoryId(1548141683189L);
        local.setClassId(1539053408647L);
        local.setClassName("pptx文件");
        local.setFileMd5("ca4dd54ace46104d1dd554409de8dd26");

//        二进制转换
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(local.getRealPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File file = new File("E:\\PptParser_testFile.pptx");

        byte[] buf = new byte[(int) file.length()];

        try {
            fstream.read(buf);
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(local);

        String fileName = local.getName();
        File temp = FileUtil.createTempFile(fileName);
        try {
            FileUtils.writeByteArrayToFile(temp, buf);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map<String, Object> extParams = new HashMap<>();

        String parserExtstr = localParser.getParserExt();
        JSONArray arrayList = JSONArray.parseArray(parserExtstr);
        if (arrayList != null) {
            //转换为目标对象list
            List<FileParserExt> parserExtList = JSONObject.parseArray(arrayList.toJSONString(), FileParserExt.class);
            extParams.put("parserExtList", parserExtList);
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("parserId", localParser.getId());


            List<FileParserExt> parserExtList = new ArrayList<>();
            FileParserExt fileParserExt1 = new FileParserExt();
            fileParserExt1.setId(1544091268042L);
            fileParserExt1.setParserId(1539050371244L);
            fileParserExt1.setParameterName("List<String>");
            fileParserExt1.setParameterDesc("黑名单");
            fileParserExt1.setParameterValue("1");
            parserExtList.add(fileParserExt1);

            FileParserExt fileParserExt2 = new FileParserExt();
            fileParserExt2.setId(1544091268239L);
            fileParserExt2.setParserId(1539050371244L);
            fileParserExt2.setParameterName("List<String>");
            fileParserExt2.setParameterDesc("白名单");
            fileParserExt2.setParameterValue("11");
            parserExtList.add(fileParserExt2);

            FileParserExt fileParserExt3 = new FileParserExt();
            fileParserExt3.setId(1544091268440L);
            fileParserExt3.setParserId(1539050371244L);
            fileParserExt3.setParameterName("List<String>");
            fileParserExt3.setParameterDesc("手动输入的关键词");
            fileParserExt3.setParameterValue("11");
            parserExtList.add(fileParserExt3);

            FileParserExt fileParserExt4 = new FileParserExt();
            fileParserExt4.setId(1544091268648L);
            fileParserExt4.setParserId(1539050371244L);
            fileParserExt4.setParameterName("int");
            fileParserExt4.setParameterDesc("关键词总数");
            fileParserExt4.setParameterValue("1");
            parserExtList.add(fileParserExt4);

            FileParserExt fileParserExt5 = new FileParserExt();
            fileParserExt5.setId(1544091268648L);
            fileParserExt5.setParserId(1539050371244L);
            fileParserExt5.setParameterName("String");
            fileParserExt5.setParameterDesc("visoPath");
            fileParserExt5.setParameterValue("E:\\");
            parserExtList.add(fileParserExt5);

            if (!ObjectUtils.isEmpty(parserExtList)) {
                extParams.put("parserExtList", parserExtList);
            }
        }

        Map<String, String> hhh = new HashMap<>();
        hhh = ParserUtil.parser(localParser, local, temp, extParams);

        System.out.println(hhh);
    }

}
