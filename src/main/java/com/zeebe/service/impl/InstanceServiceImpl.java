package com.zeebe.service.impl;

import com.zeebe.dto.TransferData;
import com.zeebe.entity.SysInstance;
import com.zeebe.exception.ServiceException;
import com.zeebe.mapper.SysInstanceMapper;
import com.zeebe.service.InstanceService;
import com.zeebe.service.MicroserviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service("InstanceService")
public class InstanceServiceImpl implements InstanceService {

    @Autowired
    private SysInstanceMapper sysInstanceMapper;

    @Autowired
    private MicroserviceService microserviceService;

    @Override
    public int createInstance(String modelName,String instanceName, String processId, List<TransferData> transferDatas){
        System.out.println("@InstanceServiceImpl: createMicroservice");
        try {
            SysInstance sysInstance = new SysInstance();
            sysInstance.setModelName(modelName);
            sysInstance.setInstanceName(instanceName);
            sysInstance.setProcessId(processId);
            String instanceParams = "";
            for(TransferData transferData: transferDatas){
               String microserviceName = transferData.getMicroserviceName();
               String jobId = transferData.getJobId();
               String jobType = transferData.getJobType();
               List<String> tdatas = transferData.getTdatas();
               String temps = "";
               int i = 0;
               for(String tdata:tdatas){
                   String temp = tdata + ",";
                   temps += temp;
               }
               temps = temps.substring(0,temps.length()-1);
               String instanceParam = jobId + "," + jobType + "," + microserviceName + "," + temps + ";";
               instanceParams += instanceParam;
            }
//            instanceParams = instanceParams.substring(0,instanceParams.length()-1);
            sysInstance.setInstanceParams(instanceParams);
            int ret = sysInstanceMapper.insert(sysInstance);
            if(ret==1){
                return 1;
            }
        }catch (Exception e){
          throw new ServiceException(e.getMessage());
        }
        return 0;
    }

    @Override
    public  SysInstance getInstanceByInstanceName(String instanceName){
        System.out.println("@InstanceServiceImpl: getInstanceByInstanceName");
        try{
            SysInstance sysInstance = sysInstanceMapper.findInstanceByInstanceName(instanceName);
            return sysInstance;
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<SysInstance>  getAllInstance(){
        System.out.println("@InstanceServiceImpl: getAllInstance");
        try{
            List<SysInstance> sysInstances = sysInstanceMapper.selectAll();
            return sysInstances;
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<TransferData> changeSysInstancetoJobWorker(String instanceName){
        System.out.println("@InstanceServiceImpl: changeSysInstancetoJobWorker");
        try{
            SysInstance sysInstance = sysInstanceMapper.findInstanceByInstanceName(instanceName);
            String instanceParams = sysInstance.getInstanceParams();
            String[] ips = instanceParams.split(";");
            List<TransferData> transferDatas = new LinkedList<>();
            for(String ip:ips){
                TransferData transferData = new TransferData();
                String[] temp = ip.split(",");
                String jobId = temp[0];
                String jobType = temp[1];
                String microserviceName = temp[2];
                String[] ipSub = Arrays.copyOfRange(temp, 3, temp.length);
                List<String> tdatas = Arrays.asList(ipSub);
                transferData.setJobId(jobId);
                transferData.setJobType(jobType);
                transferData.setMicroserviceName(microserviceName);
                transferData.setTdatas(tdatas);
                transferDatas.add(transferData);
            }
            return transferDatas;
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public int updateInstance(SysInstance record){
        System.out.println("@InstanceServiceImpl: updateInstance");
        try{
            int ret = sysInstanceMapper.updateByInstanceId(record);
            if (ret == 1){
                return 1;
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
        return 0;
    }
}
