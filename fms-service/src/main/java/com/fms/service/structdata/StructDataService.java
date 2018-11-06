package com.fms.service.structdata;

import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import com.handu.apollo.utils.exception.ApolloRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StructDataService {
    public static final String CLASSNAME = StructDataService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public void addData(Map map){
        try {
            dao.insert(CLASSNAME, "add", map);
        } catch (Exception e) {
            throw new ApolloRuntimeException(e.getCause().getMessage());
        }
    }
}
