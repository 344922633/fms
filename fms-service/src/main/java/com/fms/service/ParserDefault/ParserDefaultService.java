package com.fms.service.ParserDefault;

import com.fms.domain.filemanage.MasterSlaveDo;
import com.fms.domain.filemanage.ParserDefaultDo;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParserDefaultService {
    public static final String CLASSNAME = ParserDefaultService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public ParserDefaultDo getByName(String user) {
        return dao.get(CLASSNAME, "getByName", user);
    }

    public void add(ParserDefaultDo parserDefaultDo) {
        dao.insert(CLASSNAME, "add", parserDefaultDo);
    }

    public void delete(String user) {
        dao.delete(CLASSNAME, "delete", user);
    }
}
