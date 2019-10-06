package com.zeebe.service.impl;

import com.zeebe.dto.MicroserviceParams;
import com.zeebe.entity.SysMicroservice;
import com.zeebe.exception.ServiceException;
import com.zeebe.mapper.SysMicroserviceMapper;
import com.zeebe.service.MicroserviceService;
import com.zeebe.service.ModelService;
import com.zeebe.service.ZeebeClientService;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.DeploymentEvent;
import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.client.api.worker.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service(value = "ZeebeClientServiceImpl")
public class ZeebeClientServiceImpl implements ZeebeClientService {

    @Autowired
    private ModelService modelService;

    @Autowired
    private MicroserviceService microserviceService;

    @Autowired
    private SysMicroserviceMapper sysMicroserviceMapper;


    @Override
    public ZeebeClient createClient(String targetBorker){
        final ZeebeClient client = ZeebeClient.newClientBuilder()
                .brokerContactPoint(targetBorker)
                .build();
        return client;
    }

    @Override
    public boolean deploy(ZeebeClient client,String modelName,String bpmnProcessId){
        System.out.println("Connected.");
        final DeploymentEvent deployment = client.newDeployCommand()
                .addResourceFromClasspath(modelService.getModelLocationByModelName(modelName))
                .send()
                .join();
        int version = deployment.getWorkflows().get(0).getVersion();
        System.out.println("Workflow deployed. Version: " + version);

        final WorkflowInstanceEvent wfInstance = client.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
//                .variables(transferDatas)
                .send()
                .join();
        long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();
        System.out.println("Workflow instance created. Key: " + workflowInstanceKey);
        if(version>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean createWorker(ZeebeClient client,String jobIdT,String jobType,String url){

        final JobWorker jobWorker = client.newWorker()
                .jobType(jobType)
                .handler((jobClient,job) ->
                {
//                    String jobId = job.getElementId();
                    URL restURL = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
                    conn.setRequestMethod("GET"); // POST GET PUT DELETE
                    conn.setRequestProperty("Accept", "application/json");
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while((line = br.readLine()) != null ){
                        System.out.println(line);
                    }
                    br.close();
                    jobClient.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }).open();
        return true;
    }

    @Override
    public String transferParam(String microserviceName,List<String> tdatas) {
        System.out.println("@ZeebeClientServiceImpl: transferparam");
        try {
            SysMicroservice sysMicroservice = sysMicroserviceMapper.selectMicroserviceByMicroserviceName(microserviceName);
            String microserviceLocation = sysMicroservice.getMicroserviceLocation();
            MicroserviceParams mp = microserviceService.changeSysMicroservicetoParams(microserviceName);
            List<String> paramTypesList = mp.getParamTypes();
            int i = 0;
            String temp = "";
            String tempA = "";
            for (String ps : paramTypesList) {
                String[] paramArr = ps.split("\\s+");
                String paramType = paramArr[0];
                String paramName = paramArr[1];
                String tdata = tdatas.get(i);
                if (paramName.equals("param" + i)) {
                    if (paramType.equals("Strng")) {
                        String param = tdata;
                        temp = "param" + i + "=" + param + "&";
                    } else if (paramType.equals("int")) {
                        int param = Integer.parseInt(tdata);
                        temp = "param" + i + "=" + param + "&";
                    } else if (paramType.equals("boolean")) {
                        boolean param = Boolean.getBoolean(tdata);
                        temp = "param" + i + "=" + param + "&";
                    }
                } else {
                    return "微服务接收参数名应为：param + 数字，如:param0.";
                }
                i++;
                tempA +=temp;
            }
            tempA = tempA.substring(0,tempA.length()-1);
            String url = microserviceLocation + "?" + tempA;
            System.out.println("url1:"+url);
            return url;
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }
}
