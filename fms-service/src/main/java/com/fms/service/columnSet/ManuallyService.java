package com.fms.service.columnSet;

import com.fms.domain.columnSet.Manual;
import com.fms.domain.filemanage.ColumnValuesDo;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ManuallyService {
    public static final String CLASSNAME = ManuallyService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public List<Manual> query(Manual manual) {
        return dao.getList(CLASSNAME, "query", manual);
    }

    public List<String> getAllNzList(Manual manual) {
        return dao.getList(CLASSNAME, "getAllNzList", manual);
    }
}
