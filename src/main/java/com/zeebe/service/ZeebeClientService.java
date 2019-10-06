package com.zeebe.service;

import com.zeebe.dto.TransferData;
import com.zeebe.entity.SysMicroservice;
import io.zeebe.client.ZeebeClient;

import java.util.List;

public interface ZeebeClientService {
    ZeebeClient createClient(String targetBorker);

    boolean deploy(ZeebeClient client,String modelName,String bpmnProcessId);

    boolean createWorker(ZeebeClient client,String jobIdT,String jobType,String url);

    String transferParam(String microserviceName,List<String> paramData);

}
