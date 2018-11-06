package com.fms.service.user;

import com.fms.domain.filemanage.Directory;
import com.fms.domain.filemanage.User;
import com.handu.apollo.data.mybatis.Dao;
import com.handu.apollo.utils.CharPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final String CLASSNAME = UserService.class.getName() + CharPool.PERIOD;
    @Autowired
    private Dao dao;

    public Object getUser() {
        return dao.get(CLASSNAME, "testSelect", null);
    }

    public User queryId(User user){return dao.get(CLASSNAME, "queryId", user);}
    public User queryName(User user){return dao.get(CLASSNAME, "queryName", user);}

    public void add(User user) {
        user.setId(System.currentTimeMillis());
        dao.insert(CLASSNAME, "add", user);
    }

    public void update(User user) {
        dao.update(CLASSNAME, "update", user);
    }
}
