package com.fms.controller;

import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.FileType;
import com.fms.service.filemanage.FileParserService;
import com.fms.service.filemanage.FileService;
import com.fms.service.filemanage.FileTypeService;
import com.fms.utils.ParamUtil;
import com.google.common.collect.Maps;
import com.handu.apollo.base.Page;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  文件分类管理
 * @author drc
 */
@RestController
@RequestMapping(value = "/fileType")
public class FileTypeController {
    /**
     * 文件分类service
     */
    @Autowired
    private FileTypeService fileTypeService;
    @Autowired
    private FileService fileService;
    /**
     * 文件解析器service
     */
    @Autowired
    private FileParserService fileParserService;

    /**
     * 查询文件分类列表.
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Object getList(Map<String, Object> params, HttpServletRequest request) {
        Page page = ParamUtil.getPager(request);
        Page<FileType> p = fileTypeService.getList(params, page);
        List<FileType> list = p.getList();
        if (list != null) {
            for (FileType ft : list) {
                String fileParserIds = ft.getFileParserIds();
                if (StringUtils.isEmpty(fileParserIds)) {
                    continue;
                }

                String[] fileParserIdsArr = fileParserIds.split(",");
                String fileParserNames = fileParserService.getNamesByIds(fileParserIdsArr);
                ft.setFileParserNames(fileParserNames);

                //拆解文件后缀特殊符号
                this.unWrapperSuffix(ft);
            }
        }

        return p;
    }
    /**
     * 查询文件分类列表.
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.POST)
    public Object listAll(Map<String, Object> params, HttpServletRequest request) {
//        Page page = ParamUtil.getPager(request);
        List<FileType> list = fileTypeService.query(params);
        if (list != null) {
            for (FileType ft : list) {
                String fileParserIds = ft.getFileParserIds();
                if (StringUtils.isEmpty(fileParserIds)) {
                    continue;
                }

                String[] fileParserIdsArr = fileParserIds.split(",");
                String fileParserNames = fileParserService.getNamesByIds(fileParserIdsArr);
                ft.setFileParserNames(fileParserNames);

                //拆解文件后缀特殊符号
                this.unWrapperSuffix(ft);
            }
        }

        return list;
    }

    /**
     * 根据分类id查询分类信息.
     * @param id
     * @return
     */
    @RequestMapping("/getById")
    public Object getById(Long id) {
        FileType fileType = fileTypeService.getById(id);

        //拆解文件后缀特殊符号
        unWrapperSuffix(fileType);
        return fileType;
    }

    /**
     * 更新文件分类信息.
     * @param fileType 文件分类
     * @return
     */
    @RequestMapping("/update")
    public Object update(@ModelAttribute FileType fileType) {
        //验证参数
        String msg = fileTypeService.checkParam(fileType);
        if (StringUtils.isNotEmpty(msg)) {
            return ExtUtil.failure(msg);
        }
        if (fileType.getId() == null) {
            return ExtUtil.failure("id为空！");
        }

        //判断名称是不是存在
        int num = fileTypeService.getCountByName(fileType);
        if (num > 0) {
            return ExtUtil.failure("分类名称已经存在！");
        }

        fileType.setUpdated(new Date());
        //包装文件后缀
        wrapperSuffix(fileType);
        fileTypeService.update(fileType);
        return ExtUtil.success("更新成功！");
    }

    /**
     * 添加文件分类信息.
     * @param fileType 文件分类
     * @return
     */
    @RequestMapping("/add")
    public Object add(@ModelAttribute FileType fileType) {
        //验证参数
        String msg = fileTypeService.checkParam(fileType);
        if (StringUtils.isNotEmpty(msg)) {
            return ExtUtil.failure(msg);
        }
        //判断名称是不是存在
        int num = fileTypeService.getCountByName(fileType);
        if (num > 0) {
            return ExtUtil.failure("分类已经存在！");
        }

        fileType.setCreated(new Date());
        fileType.setUpdated(new Date());
        //包装文件后缀
        wrapperSuffix(fileType);
        fileTypeService.add(fileType);
        return ExtUtil.success("添加成功！");
    }
    /**
     * 根据分类id查询分类信息.
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public Object delById(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("classId", id+"");
        List<File>  files = fileService.query(params);
        if(files!=null&&files.size()>0){
            return ExtUtil.failure("有文件引用该分类不允许删除！");
        }
        fileTypeService.delete(id);
        return ExtUtil.success("删除成功！");
    }

    /**
     * 对文件后缀，用<>符号进行包装
     * @param fileType
     */
    private void wrapperSuffix(FileType fileType) {
        if (fileType == null || StringUtils.isEmpty(fileType.getFileSuffix())) {
            return;
        }

        StringBuffer suffixBuffer = new StringBuffer();
        String[] suffixArr = fileType.getFileSuffix().split(",");
        for (String s : suffixArr) {
            suffixBuffer.append("<");
            suffixBuffer.append(s);
            suffixBuffer.append(">");
            suffixBuffer.append(",");
        }

        if (suffixBuffer.length() > 0) {
            String suffix = suffixBuffer.toString().substring(0, suffixBuffer.toString().length() - 1);
            fileType.setFileSuffix(suffix);
        }
    }

    /**
     * 拆解文件后缀的<>符号包装
     * @param fileType
     */
    private void unWrapperSuffix(FileType fileType) {
        if (fileType == null || StringUtils.isEmpty(fileType.getFileSuffix())) {
            return;
        }

        StringBuffer suffixBuffer = new StringBuffer();
        String suffix = fileType.getFileSuffix().replaceAll("<", "").replaceAll(">", "");
        fileType.setFileSuffix(suffix);
    }
}
