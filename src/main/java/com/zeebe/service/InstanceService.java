package com.zeebe.service;


import com.zeebe.dto.TransferData;
import com.zeebe.entity.SysInstance;

import java.util.List;

public interface InstanceService {
    int createInstance(String modelName,String instanceName, String processId, List<TransferData> transferData);

    List<SysInstance>  getAllInstance();

    SysInstance getInstanceByInstanceName(String instanceName);

    List<TransferData> changeSysInstancetoJobWorker(String instanceName);

    int updateInstance(SysInstance record);
}
