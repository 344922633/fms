package com.fms.service.masterSlave;

import com.fms.domain.filemanage.ColumnValuesDo;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnValuesService {
    public static final String CLASSNAME = ColumnValuesService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public List<ColumnValuesDo> query(ColumnValuesDo columnValuesDo) {
        return dao.getList(CLASSNAME, "query", columnValuesDo);
    }

}
