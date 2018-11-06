package com.fms.controller.filemanage;

import com.anniweiya.fastdfs.FastDFSTemplate;
import com.anniweiya.fastdfs.exception.FastDFSException;
import com.fms.domain.filemanage.Directory;
import com.fms.domain.filemanage.File;
import com.fms.service.filemanage.DirectoryService;
import com.fms.service.filemanage.FileService;
import com.google.common.collect.Maps;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件目录
 */
@RestController
public class DirectoryController {
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private FileService fileService;
    @Autowired
    private FastDFSTemplate fastDFSTemplate;

    /**
     * 根据目录（文件夹）ID获取目录信息
     * @param id
     * @return
     */
    @RequestMapping("getDirsByParentId")
    public Object getDirsByParentId(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentId", id);
        return directoryService.query(params);
    }

    /**
     * 获取目录树
     * @return
     */
    @RequestMapping("getDirTree")
    public Object getDirTree() {
        List<Directory> dirs = directoryService.query(null);
        return TreeUtil.listToTree(dirs);
    }

    /**
     * 新增文件目录
     * @param dir
     * @return
     */
    @RequestMapping("addDir")
    public Object addDir(@ModelAttribute Directory dir) {
        directoryService.add(dir);
        return ExtUtil.success("添加成功！");
    }

    /**
     * 修改文件目录信息
     * @param dir
     * @return
     */
    @RequestMapping("updateDir")
    public Object updateDir(@ModelAttribute Directory dir) {
        if (dir.getParentId() == null) {
            return ExtUtil.failure("父节点不允许为空！");
        }
        directoryService.update(dir);
        return ExtUtil.success("添加成功！");
    }

    /**
     * 删除文件目录(级联删除同时删除文件夹下所有文件)
     * @param id
     * @return
     */
    @RequestMapping("deleteDir")
    public Object deleteDir(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        if (id.intValue() == 1) {
            return ExtUtil.failure("无法删除根目录！");
        }
        params.put("parentId", id);
        List<Directory> dirs = directoryService.query(params);
        List<Directory> childrenDirs = new ArrayList<Directory>();
        grtAllChildren(dirs,childrenDirs);
//        if (CollectionUtil.isNotEmpty(dirs)) {
//            return ExtUtil.failure("该目录非子目录无法删除！");
//        }
//        params.clear();
//        params.put("directoryId", id);
//        int count = fileService.queryCount(params);
//        if (count > 0) {
//            return ExtUtil.failure("该目录下存在文件无法删除！");
//        }
//
//        directoryService.delete(id);

        List<Long> dIds = new ArrayList<Long>();
        dIds.add(id);
        dirs.addAll(childrenDirs);
        for(Directory tDir:dirs){
            dIds.add(tDir.getId());
        }

        Map<String, Object> pms = Maps.newHashMap();
        pms.put("ids",dIds);

        List<File> fileList = fileService.getFileListByDirectoryIds(pms);
//        List<Long> ids = new ArrayList<Long>();
        for(File file:fileList){
//            ids.add(fileparser.getId());
            try {
                fastDFSTemplate.deleteFile(file.getGroups(), file.getRealPath());
                fileService.delete(file.getId());
            } catch (FastDFSException e) {
                fileService.delete(file.getId());
                e.printStackTrace();
            } catch (NullPointerException e) {
                fileService.delete(file.getId());
                e.printStackTrace();
            }catch (Exception e){
                fileService.delete(file.getId());
                e.printStackTrace();
            }
        }

        directoryService.deleteIds(pms);

//        for(Long stid : ids){
//            File fileparser = fileService.get(stid);
//            if (fileparser == null) {
//                return ExtUtil.failure("文件不存在！");
//            }
//            try {
//                fastDFSTemplate.deleteFile(fileparser.getGroups(), fileparser.getRealPath());
//                fileService.delete(stid);
//            } catch (FastDFSException e) {
//                if (e.getCause() instanceof NullPointerException) {
//                    fileService.delete(stid);
//                }
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                fileService.delete(stid);
//            }
//        }

        return ExtUtil.success("删除成功！");
    }

    /**
     * 递归获取指定目录下所有子目录
     * @param dirs
     * @param childrenDirs
     */
    public void grtAllChildren(List<Directory> dirs,List<Directory> childrenDirs){

            if(dirs==null || dirs.size()==0){

            }else {
                for (Directory dir : dirs) {
                    Map<String, Object> params = Maps.newHashMap();
                    params.put("parentId", dir.getId());
                    List<Directory> tDirs = directoryService.query(params);
                    childrenDirs.addAll(tDirs);
                    grtAllChildren(tDirs, childrenDirs);
                }
            }
    }
}
