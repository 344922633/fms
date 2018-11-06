package com.fms.service.filemanage;

import com.fms.domain.filemanage.FileParserJar;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FileParserJarService {

    public static final String CLASSNAME = FileParserJarService.class.getName() + CharPool.PERIOD;

    @Autowired
    private Dao dao;

    public List<FileParserJar> query(Map<String, Object> params){return dao.getList(CLASSNAME,"query",params);}
    public void delete(Map<String, Object> params){ dao.delete(CLASSNAME,"delete",params);}
    public void update(Map<String, Object> params){dao.update(CLASSNAME,"update",params);}
    public void add(FileParserJar fileParserJar){
        fileParserJar.setId(System.currentTimeMillis());
        dao.insert(CLASSNAME,"add",fileParserJar);
    }
}
