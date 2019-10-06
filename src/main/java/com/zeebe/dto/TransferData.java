package com.zeebe.dto;

import java.util.List;

public class TransferData {
    public String jobType;
    public String jobId;
    public String microserviceName;
    public List<String> tdatas;

    public String getJobType() {
        return jobType;
    }
    public void setJobType(String jobType) {
        this.jobType = jobType == null ? null : jobType.trim();
    }

    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId == null ? null : jobId.trim();
    }

    public String getMicroserviceName() {
        return microserviceName;
    }
    public void setMicroserviceName(String microserviceName) {
        this.microserviceName = microserviceName == null ? null : microserviceName.trim();
    }

    public List<String> getTdatas(){return tdatas;}
    public void setTdatas(List<String> tdatas){
        this.tdatas = tdatas == null ? null : tdatas;
    }
}
