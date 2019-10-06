package com.zeebe.controller;


import com.zeebe.dto.TransferData;
import com.zeebe.dto.ResponseJson;
import com.zeebe.entity.SysInstance;
import com.zeebe.service.InstanceService;
import com.zeebe.service.MicroserviceService;
import com.zeebe.service.ZeebeClientService;
import com.zeebe.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping(value = "/instance")
public class InstaneController {
    @Autowired
    private InstanceService instanceService;

    @Autowired
    private MicroserviceService microserviceService;

    @Autowired
    private ZeebeClientService zeebeClientService;

    @PostMapping(value="createInstance")
    public ResponseJson createInstance(@RequestBody List<TransferData> transferDatas){
//        String modelName = request.getParameter("modelName");
//        String processId = request.getParameter("processId");
        String modelName = "test0";
        String instanceName = "testInstance0";
        String processId = "text0-Id";
        int createInstance = instanceService.createInstance(modelName,instanceName,processId, transferDatas);
        if(createInstance == 1){
            return ResponseUtil.ok("创建实例成功");
        }else {
            return ResponseUtil.error("创建实例失败");
        }
    }

    @GetMapping(value="getAllInstance")
    public ResponseJson getAllInstance(){
        try{
            List<SysInstance> sysInstances = instanceService.getAllInstance();
            return ResponseUtil.ok(sysInstances);
        }catch (Exception e){
            return ResponseUtil.error("查询失败");
        }
    }

    @PostMapping(value = "/updateInstance")
    public ResponseJson updateInstance(@RequestBody SysInstance sysInstance){
        if(sysInstance != null){
            instanceService.updateInstance(sysInstance);
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("更新实例失败");
    }
}
