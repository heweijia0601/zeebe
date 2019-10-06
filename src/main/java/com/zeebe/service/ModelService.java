package com.zeebe.service;

import com.zeebe.entity.SysModel;
import org.springframework.web.multipart.MultipartFile;

public interface ModelService {
    SysModel store(SysModel sysModel, MultipartFile file);

    String getModelLocationByModelName(String ModelName);
}
