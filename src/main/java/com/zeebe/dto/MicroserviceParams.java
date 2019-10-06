package com.zeebe.dto;

import java.util.List;

public class MicroserviceParams {
    public String microserviceName;
    public List<String> paramTypes;
    public int paramsSize;

    public String getMicroserviceName() {
        return microserviceName;
    }
    public void setMicroserviceName(String microserviceName) {
        this.microserviceName = microserviceName == null ? null : microserviceName.trim();
    }

    public List<String> getParamTypes(){return paramTypes;}
    public void setParamTypes(List<String> paramTypes){
        this.paramTypes = paramTypes == null ? null : paramTypes;
    }

    public int getParamsSize() {return paramsSize;}
    public void setParamsSize(int paramsSize)  {this.paramsSize = paramsSize;}

    @Override
    public String toString(){
        String temp = "";
        int i = 1;
        for(String par:paramTypes){
            temp += par + ",";
            i++;
        }
        temp = temp.substring(0,temp.length()-1);
//        return "{"
//                + "microserviceName:" + microserviceName
//                + ",params:[" + tepm + "]"
//                + ",paramsSize:" + paramsSize
//                +"}";
        return temp;
    }
}


