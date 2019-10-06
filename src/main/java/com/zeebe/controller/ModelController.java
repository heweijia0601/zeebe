package com.zeebe.controller;

import com.zeebe.dto.ResponseJson;
import com.zeebe.entity.SysModel;
import com.zeebe.service.ModelService;
import com.zeebe.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/model")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @PostMapping(value = "")
    public ResponseJson upload( @RequestParam("modelName")String modelName,
                                @RequestParam(value = "remark",required = false) String remark,
                               @RequestParam("file") MultipartFile file) {

        SysModel sysModel = new SysModel();
        sysModel.setModelName(modelName);
        sysModel.setRemark(remark);
        SysModel model = modelService.store(sysModel,file);
        if(model != null){
            return ResponseUtil.ok(model);
        }
        return ResponseUtil.error("上传文件失败");
    }
}
