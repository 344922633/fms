package com.fms.service.filemanage;

import com.fms.domain.filemanage.File;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class FileParserStatusService {
    public static final String CLASSNAME = FileParserStatusService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public Integer getAllFileAmount() {
        return dao.get(CLASSNAME, "getAllFileAmount", null);

    }

    public Integer getParsedFileAmount() {
        return dao.get(CLASSNAME, "getParsedFileAmount", 1);

    }

    public Integer getUnresolvedFileAmount() {
        int unsolved;
        return unsolved= dao.get(CLASSNAME, "getUnresolvedFileAmount", 0);

    }

}
