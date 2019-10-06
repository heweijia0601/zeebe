package com.zeebe.controller;

import com.zeebe.dto.ResponseJson;
import com.zeebe.entity.SysMicroservice;
import com.zeebe.service.MicroserviceService;
import com.zeebe.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/microservice")
public class MicroserviceController {
    @Autowired
    private MicroserviceService microserviceService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/createMicroservice")
    public ResponseJson createMicroservice(@RequestParam(value= "microserviceName")String microserviceName,
                                           @RequestParam(value = "microserviceLocation") String microserviceLocation,
                                           @RequestParam(value = "description")String description,
                                           @RequestParam(value = "paramTypes")List<String> paramTypes){
        int temp = microserviceService.createMicroservice(microserviceName,microserviceLocation,description,paramTypes);
        if (temp == 1){
            return ResponseUtil.ok();
        }else {
            return ResponseUtil.error("创建微服务"+ microserviceName + "失败");
        }

    }

    @PostMapping(value = "/deleteMicroserviceByMicroserviceId")
    public ResponseJson deleteMicroserviceByMicroserviceId(@RequestParam(value = "MicroserviceIds")List<Integer> microserviceIds){
        HashMap<Integer,String> map = new HashMap<Integer, String>();
        try{
            for(Integer microserviceId : microserviceIds){
                int ret = microserviceService.deleteMicroserviceByMicroserviceId(microserviceId);
                if (ret == 1){
                    map.put(microserviceId,"删除成功");
                }else{
                    map.put(microserviceId,"删除失败");
                }
            }
         return ResponseUtil.ok(map);
        }catch (Exception e){
            return ResponseUtil.error("删除微服务操作未完成");
        }
    }

    @PostMapping(value = "/updateMicroservice")
    public ResponseJson updateMicroservice(@RequestBody SysMicroservice sysMicroservice){
        if(sysMicroservice != null){
            microserviceService.updateMicroservice(sysMicroservice);
            return ResponseUtil.ok();
        }
        return ResponseUtil.error("更新微服务失败");
    }

    @GetMapping(value = "/getAllMicroservice")
    public ResponseJson getAllMicroservice(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<SysMicroservice> microserviceList = microserviceService.getAllMicroservice();
        for(SysMicroservice microservice:microserviceList) {
            String microserviceName = microservice.getMicroserviceName();
            List<String> services = discoveryClient.getServices();
            for (String service : services) {
//                System.out.println("service:" + service);
                System.out.println(microserviceName+":"+microserviceName.contains(service));
                if(microserviceName.contains(service)){
                    microservice.setStatus(1);
                    break;
                }else {
                    microservice.setStatus(0);
                    break;
                }
            }
            microserviceService.updateMicroservice(microservice);
            System.out.println(microserviceName+":"+microservice.getStatus());
            System.out.println("-------------------------------------------------");
        }
        HashMap<String,Object> map = new HashMap<String,Object>();
        PageInfo pageInfo = new PageInfo(microserviceList);
        if(microserviceList != null){
            pageInfo.setList(microserviceList);
            map.put("pageInfo", pageInfo);
            map.put("pageTotal", pageInfo.getTotal());
        }
        return ResponseUtil.ok(map);
    }

    @GetMapping("/serviceurl")
    public ResponseJson serviceUrl() {
        HashMap<String, List<ServiceInstance>> map = new HashMap<>();
        List<String> services = discoveryClient.getServices();

        for (String service : services) {
            System.out.println("service:"+service);
            List<ServiceInstance> sis = discoveryClient.getInstances(service);
            map.put(service, sis);
        }
        return ResponseUtil.ok(map);
    }

}
