package com.fms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.service.structdata.StructDataService;
import com.fms.utils.ParamUtil;
import com.handu.apollo.utils.ExtUtil;
import com.handu.apollo.utils.exception.ApolloRuntimeException;
import com.handu.apollo.utils.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class StructDataController {
    @Autowired
    private StructDataService structDataService;

    @RequestMapping("addData")
    public Object addData(HttpServletRequest request) {
        ObjectMapper mapper = JsonUtil.getMapper();
        String mapping = ParamUtil.get(request, "mapping", null);
        String result = ParamUtil.get(request, "result", null);
        try {
            List<Map> list = mapper.readValue(result, new TypeReference<List<Map>>() {});
            for (Map map : list) {
                structDataService.addData(map);
            }
            System.out.println(list.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApolloRuntimeException e) {
            return ExtUtil.failure(e.getMessage());
        }
        return ExtUtil.success("保存成功！");
    }
}
