package com.zeebe.service.impl;

import com.zeebe.constant.ConstantProperties;
import com.zeebe.entity.SysModel;
import com.zeebe.exception.ServiceException;
import com.zeebe.service.ModelService;
import com.zeebe.mapper.SysModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

@Service("modelService")
public class ModelServiceImpl implements ModelService {

    @Autowired
    private SysModelMapper sysModelMapper;

    private Path rootLocation;

    @Autowired
    public ModelServiceImpl(ConstantProperties constantProperties) {
        this.rootLocation = Paths.get(constantProperties.getUploadDir());
    }

    @Override
    public SysModel store(SysModel sysModel, MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileType= filename.substring(filename.lastIndexOf("."),filename.length());
        String realFilename = UUID.randomUUID().toString() + fileType;
        try {
            if (file.isEmpty()) {
                throw new ServiceException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new ServiceException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(realFilename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        try {
            sysModel.setModelName(sysModel.getModelName());
            sysModel.setModelLocation(realFilename);
            sysModel.setCreateTime(new Date());
            sysModel.setStatus(0);
            if(sysModelMapper.insert(sysModel)==1) {
                System.out.println("插入数据成功");
                return sysModel;
            }
        }
        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return null;
    }

    @Override
    public String getModelLocationByModelName(String modelName){
        try {
            SysModel sysModel = sysModelMapper.findModelLoationByModelName(modelName);
            String modelLocation = sysModel.getModelLocation();
            return modelLocation;
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }
}
