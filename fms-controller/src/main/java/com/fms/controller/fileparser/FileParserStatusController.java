package com.fms.controller.fileparser;

import com.fms.service.filemanage.FileParserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class FileParserStatusController {

    @Autowired
    private FileParserStatusService fileParserStatusService;

    //获取解析文件信息
    @RequestMapping("getAnalyseFiles")
    public  List<Map<String,Object>> getAllFilesAmount() {

        //获取所有文件数
        Integer allFileAmount = fileParserStatusService.getAllFileAmount();
        
        //获取已解析文件总数
        Integer parsedFileAmount = fileParserStatusService.getParsedFileAmount();
        
        //获取未解析文件总数
        Integer UnresolvedFileAmount = Integer.valueOf(fileParserStatusService.getUnresolvedFileAmount());

        //获取已解析文件所占百分比
        String Percent = getPercent(allFileAmount,parsedFileAmount);

        //封装数据集合
        Map<String,Object> data = new HashMap<>();
        data.put("allFileAmount",allFileAmount);
        data.put("parsedFileAmount",parsedFileAmount);
        data.put("UnresolvedFileAmount",UnresolvedFileAmount);
        data.put("Percent",Percent);

        List<Map<String,Object>> list = new ArrayList<>();
        list.add(data);

        return list;
    }

    //计算百分比
    private String getPercent(Integer allFileAmount, Integer parsedFileAmount) {

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        String percent = numberFormat.format((float)parsedFileAmount/(float)allFileAmount * 100);

        return percent + "%";
    }

   /* //获取已解析文件总数
    @RequestMapping("getParBUsedFileAmount")
    public Integer getParsedFileAmount() {
        return fileParserStatusService.getParsedFileAmount();
    }*/

   /* //获取未解析文件总数
    @RequestMapping("getUnresolvedFileAmount")
    public Integer getUnresolvedFileAmount() {
        return Integer.valueOf(fileParserStatusService.getUnresolvedFileAmount());
    }*/
    //未解析文件所占百分比
/*    @RequestMapping("getUnresolvedPercent")
    public String getUnresolvedPercent() {


        return     String .format("%.2f",(Integer.valueOf(fileParserStatusService.getUnresolvedFileAmount())/Integer.valueOf(fileParserStatusService.getAllFileAmount())));



    }*/


    }
