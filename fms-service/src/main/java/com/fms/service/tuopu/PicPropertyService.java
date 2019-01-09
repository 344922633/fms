package com.fms.service.tuopu;

import com.fms.domain.tuopu.PicProperty;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PicPropertyService {
    public static final String CLASSNAME = PicPropertyService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public void add(PicProperty picProperty){
        dao.insert(CLASSNAME,"add",picProperty);
    }

}
