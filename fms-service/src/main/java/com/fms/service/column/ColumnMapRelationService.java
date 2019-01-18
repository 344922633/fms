package com.fms.service.column;

import com.alibaba.druid.sql.visitor.functions.If;
import com.fms.domain.schema.ColumnDic;
import com.fms.domain.schema.ColumnMapRelation;
import com.fms.service.filemanage.ChunkService;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ColumnMapRelationService {

    public static final String CLASSNAME = ColumnMapRelationService.class.getName() + CharPool.PERIOD;

    @Autowired
    private Dao dao;

    public Map<String, Object> getColumnMapRelation(List<String> columnKeys) {

        //根据columnKeys查询模板信息
        List<Map<String, Object>> templateNameInfos = dao.getList(CLASSNAME, "getTemplateNameByColumnKeys", columnKeys);


        List<ColumnMapRelation> columnMapRelations = new ArrayList<>();
        if (templateNameInfos != null && templateNameInfos.size() > 0){
            //获取模板名称
            String templateName = (String) templateNameInfos.get(0).get("templateName");

            //根据模板名称查询记录
            columnMapRelations = getColumnMapRelationByTemplateName(templateName);

        }

        //创建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("templateNameInfos", templateNameInfos);
        data.put("columnMapRelations", columnMapRelations);

        return data;
    }


    public List<ColumnMapRelation> getColumnMapRelationByTemplateName(String templateName) {
       return dao.getList(CLASSNAME, "getColumnMapRelationByTemplateName", templateName);
    }



    /**
     * 添加columnMapRelation记录
     *
     */
    public void addColumnMapRelations(List<ColumnMapRelation> addColumnMapRelations) {

        List<ColumnMapRelation> formColumnMapRelations = findAll();

        for (ColumnMapRelation columnMapRelation : addColumnMapRelations) {

            if(columnMapRelation.getId() == null){
                columnMapRelation.setId(isColumnMapRelationExists(columnMapRelation, formColumnMapRelations));
            }

            if (columnMapRelation.getId() != null) {
                //修改column_map_relation记录
                dao.update(CLASSNAME, "updateColumnMapRelationById", columnMapRelation);
                addOrUpdateColumnDics(columnMapRelation.getDicMap(),columnMapRelation.getId(),"2");
            } else {
                //添加
                long id = System.currentTimeMillis();
                columnMapRelation.setId(id);
                dao.insert(CLASSNAME, "insertColumnMapRelation", columnMapRelation);
                addOrUpdateColumnDics(columnMapRelation.getDicMap(),id,"1");
            }

        }
    }

    private List<ColumnDic> findColumnDics() {
        return dao.getList(CLASSNAME,"findColumnDics",null);
    }

    /**
     * 批量插入columnDic对象
     * @param dicMap 数据
     * @param id
     * @param type 操作类型，1：添加，2添加或修改
     */
    private void addOrUpdateColumnDics(Map<String, Object> dicMap,long id,String type) {

        //查询所有的column_dic表记录
        List<ColumnDic> columnDics = findColumnDics();

        //遍历map
        for (Map.Entry<String, Object> entry : dicMap.entrySet()) {
            //获取key
            String key = entry.getKey();
            //查看是否当前key是否需要存储
            if (key.startsWith("nz_dic")) {
                //需要存储,columnDic创建对象
                String value =  entry.getValue() + "";

                ColumnDic columnDic = new ColumnDic(id,key,value);
                if("1".equals(type)){
                    addColumnDic(columnDic);
                }else{
                    //查看columnDic是否存在记录
                    if(isColumnDicExists(columnDics,id,key)){
                        //修改
                        updateColumnDicByIdAndKye(columnDic);
                    }else{
                        //添加
                        addColumnDic(columnDic);
                    }
                }
            }
        }

    }

    private void updateColumnDicByIdAndKye(ColumnDic columnDic) {
        dao.update(CLASSNAME, "updateColumnDicByIdAndKye", columnDic);
    }

    private boolean isColumnDicExists(List<ColumnDic> columnDics, long id, String key) {

        for (ColumnDic columnDic : columnDics) {
            //条件判断，根据id和key
            boolean flag = columnDic.getColumnMapId() == id && columnDic.getDicName().equals(key);

            if(flag){
                return true;
            }
        }

        return false;

    }

    //插入columnDic对象
    public void addColumnDic(ColumnDic columnDic){
        //插入数据
        dao.insert(CLASSNAME, "insertColumnDic", columnDic);
    }

    /**
     * @return 查询所有的记录
     */
    public List<ColumnMapRelation> findAll() {
        return dao.getList(CLASSNAME, "findAll", null);
    }


    /**
     * 判断模板名称和列名称是否在数据库存在
     *
     * @param columnMapRelation
     * @param formColumnMapRelations
     * @return
     */
    private Long isColumnMapRelationExists(ColumnMapRelation columnMapRelation, List<ColumnMapRelation> formColumnMapRelations) {

        for (ColumnMapRelation formColumnMapRelation : formColumnMapRelations) {
            boolean flag = formColumnMapRelation.getTemplateName().equals(columnMapRelation.getTemplateName())
                    && formColumnMapRelation.getColumnKey().equals(columnMapRelation.getColumnKey());
            if (flag) {
                return formColumnMapRelation.getId();
            }
        }

        return null;
    }

}
