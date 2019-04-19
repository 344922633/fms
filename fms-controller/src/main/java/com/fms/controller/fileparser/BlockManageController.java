
package com.fms.controller.fileparser;

import com.fms.domain.filemanage.BlockManage;
import com.fms.service.filemanage.BlockManageService;
import com.fms.utils.ExcelUtil;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 黑、白名单管理入口.
 * @author drc
 */

@RestController
@RequestMapping("/blockManage")
public class BlockManageController {

/**
     * 黑、白名单管理service
     */

    @Autowired
    private BlockManageService blockManageService;


/**
     * 查询文件分类列表.
     * @param params
     * @return
     */
//犹豫预览发送请求本机无法调试   只能暂时在显示的时候进行数组重组
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Object getList(@RequestParam Map<String, Object> params) {
        List<BlockManage> blockManageList= blockManageService.getList(params);
        return blockManageList;
    }


/**
     * 根据id更新名单信息..
     * @param blockManage
     * @return
     */

    @RequestMapping(value = "/updateById", method = RequestMethod.POST)
    public Object updateById(@ModelAttribute BlockManage blockManage) {
        blockManage.setUpdated(new Date());
        blockManageService.updateById(blockManage);
        return ExtUtil.success("更新成功！");
    }


/**
     * 根据id更新名单信息..
     * @param blockManage
     * @return
     */

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@ModelAttribute BlockManage blockManage) {
        blockManage.setUpdated(new Date());
        blockManage.setCreated(new Date());
        blockManageService.add(blockManage);
        return ExtUtil.success("更新成功！");
    }


/**
     * 导入名单信息.
     * @param file
     * @return
     */

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Object importFile(MultipartFile file, Long fileParserId) {
        //判断文件是否为空
        if(file==null || file.getSize() == 0) {
            return ExtUtil.failure("请选择为空！");
        }

        List<List<String>> list = null;
        //执行解析文件操作
       try {
           list = ExcelUtil.readXls(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            return ExtUtil.failure("解析数据异常：" + e.getMessage());
        }

        if (!CollectionUtil.isNotEmpty(list) || list.size() < 1) {
            return ExtUtil.failure("文件内容为空！");
        }

        //导入内容
        System.out.println(list);

        //白名单、黑名单buffer
        StringBuffer whiteBuffer =  new StringBuffer();
        StringBuffer blackBuffer =  new StringBuffer();
        for (List<String> rows : list) {
            String blackContent = rows.get(0);
            if (StringUtils.isNotEmpty(blackContent)) {
                blackBuffer.append(blackContent);
                blackBuffer.append(",");
            }

            //如果小于等于0，说明第二项内容为空
            if (rows.size() <= 1) {
                continue;
            }
            String whiteContent = rows.get(1);
            if (StringUtils.isNotEmpty(whiteContent)) {
                whiteBuffer.append(whiteContent);
                whiteBuffer.append(",");
            }
        }

        String wihte = whiteBuffer.toString();
        String black = blackBuffer.toString();
        if (StringUtils.isNotEmpty(wihte)) {
            wihte = wihte.substring(0, wihte.length() - 1);
        }
        if (StringUtils.isNotEmpty(black)) {
            black = black.substring(0, black.length() - 1);
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("white", wihte);
        result.put("black", black);
        return result;
    }
}

