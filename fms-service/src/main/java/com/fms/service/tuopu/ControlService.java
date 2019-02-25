package com.fms.service.tuopu;

import com.fms.domain.filemanage.*;
import com.fms.domain.tuopu.Control;
import com.fms.domain.tuopu.ControlProperty;
import com.fms.domain.tuopu.Picture;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 控件服务实现类.
 *
 * @author drc
 */
@Service
@Transactional
public class ControlService {
    public static final String CLASSNAME = ControlService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public List<Control> getList(Map<String, Object> params) {
        return dao.getList(CLASSNAME, "getList", params);
    }

    public void add(Control control) {
        dao.insert(CLASSNAME, "add", control);
    }
    public void update(Control control) {
        dao.update(CLASSNAME, "update", control);
    }
    public List<Control> getControl(String name) {
        return dao.getList(CLASSNAME, "getControl", name);
    }
    public int query(String id) {
        return dao.get(CLASSNAME, "query", id);
    }
    public void delete(String id) {
        dao.delete(CLASSNAME, "delete", id);
    }

    public void update(Picture picture) {
        dao.update(CLASSNAME, "update", picture);
    }

    public void updateControl(Control control) {
        dao.update(CLASSNAME, "update", control);
    }

    public List<ControlProperty> queryPropertyById(String controlId) {
        return dao.getList(CLASSNAME, "queryPropertyById", controlId);
    }
}