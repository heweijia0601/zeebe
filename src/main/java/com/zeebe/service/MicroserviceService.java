package com.zeebe.service;

import com.zeebe.dto.MicroserviceParams;
import com.zeebe.entity.SysMicroservice;

import java.util.List;

public interface MicroserviceService {
    int createMicroservice(String microserviceName, String microserviceLocation, String description, List<String> params);

    int deleteMicroserviceByMicroserviceId(Integer microserviceId);

    List<SysMicroservice> getAllMicroservice();

    int updateMicroservice(SysMicroservice record);

    String getMicroserviceLocationByMicroserviceName(String microserviceName);

    MicroserviceParams changeSysMicroservicetoParams(String microserviceName);

    SysMicroservice getMicroserviceByMicroserviceName(String microserviceName);

}
