package com.zeebe.service.impl;

import com.zeebe.dto.MicroserviceParams;
import com.zeebe.entity.SysMicroservice;
import com.zeebe.exception.ServiceException;
import com.zeebe.mapper.SysMicroserviceMapper;
import com.zeebe.service.MicroserviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
@Service("MicroserviceService")
public class MicroserviceServiceImpl implements MicroserviceService {

    @Autowired
    private SysMicroserviceMapper sysMicroserviceMapper;

    @Override
    public int createMicroservice(String microserviceName, String microserviceLocation, String description, List<String> paramTypes){
        System.out.println("@MicroserviceServiceImp: createMicroservice");
        try {
            SysMicroservice sysMicroservice = new SysMicroservice();
            sysMicroservice.setMicroserviceName(microserviceName);
            sysMicroservice.setMicroserviceLocation(microserviceLocation);
            sysMicroservice.setDescription(description);

            MicroserviceParams mp = new MicroserviceParams();
            mp.setMicroserviceName(sysMicroservice.getMicroserviceName());
            mp.setParamTypes(paramTypes);
            mp.setParamsSize(paramTypes.size());

            sysMicroservice.setParams(mp.toString());

            int ret = sysMicroserviceMapper.insert(sysMicroservice);
            if(ret == 1){
                return 1;
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
        return 0;
    }

    @Override
    public int deleteMicroserviceByMicroserviceId(Integer microserviceId){
        System.out.println("@MicroserviceServiceImp: deleteMicroserviceByMicroserviceId");
        try {
            int ret = sysMicroserviceMapper.deleteByMicroserviceId(microserviceId);
            if (ret == 1){
                return 1;
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
        return 0;
    }

    @Override
    public List<SysMicroservice> getAllMicroservice(){
        System.out.println("@MicroserviceServiceImp: getAllMicroservice");
        try{
           return sysMicroserviceMapper.selectAll();
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public int updateMicroservice(SysMicroservice record){
        System.out.println("@MicroserviceServiceImp: updateMicroservice");
        try{
            int ret = sysMicroserviceMapper.updateByMicroserviceId(record);
            if (ret == 1){
                return 1;
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
        return 0;
    }

    @Override
    public String getMicroserviceLocationByMicroserviceName(String microserviceName){
        System.out.println("@MicroserviceServiceImp: getMicroserviceLocationByMicroserviceName");
        try {
            return sysMicroserviceMapper.findMicroserviceLocationByMicroserviceName(microserviceName);
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public MicroserviceParams changeSysMicroservicetoParams(String microserviceName){
        System.out.println("@MicroserviceServiceImp: changeSysMicroservicetoParams");
        try{
            SysMicroservice sysMicroservice =sysMicroserviceMapper.selectMicroserviceByMicroserviceName(microserviceName);
            String mp = sysMicroservice.getParams();
            String[] mpl = mp.split(",");
            List<String> list = Arrays.asList(mpl);
            MicroserviceParams params = new MicroserviceParams();
            params.setMicroserviceName(microserviceName);
            params.setParamTypes(list);
            params.setParamsSize(list.size());
            return params;
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public SysMicroservice getMicroserviceByMicroserviceName(String microserviceName){
        System.out.println("@MicroserviceServiceImp: getMicroserviceLocationByMicroserviceName");
        try {
            return sysMicroserviceMapper.selectMicroserviceByMicroserviceName(microserviceName);
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }
}
