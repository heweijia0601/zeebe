package com.zeebe.controller;

import com.zeebe.dto.TransferData;
import com.zeebe.dto.ResponseJson;
import com.zeebe.entity.SysInstance;
import com.zeebe.entity.SysMicroservice;
import com.zeebe.mapper.SysMicroserviceMapper;
import com.zeebe.service.InstanceService;
import com.zeebe.service.MicroserviceService;
import com.zeebe.service.ModelService;
import com.zeebe.service.ZeebeClientService;
import com.zeebe.utils.ResponseUtil;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.DeploymentEvent;
import io.zeebe.client.api.response.WorkflowInstanceEvent;

import io.zeebe.client.api.worker.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


@RestController
@RequestMapping(value = "/zeebeClient")
public class ZeebeClientController {

    @Autowired
    private ModelService modelService;
    @Autowired
    private ZeebeClientService zeebeClientService;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private MicroserviceService microserviceService;
    @Autowired
    private SysMicroserviceMapper sysMicroserviceMapper;

    @GetMapping(value = "/zeebeClientRun")
    public ResponseJson zeebeClientRun(@RequestParam(value ="targetBorker")String targetBorker,
                                       @RequestParam(value ="instanceName")String instanceName){
//        List<List<String>> params = new LinkedList<>();
//        List<String>p0 = new LinkedList<>();
//        List<String>p1 = new LinkedList<>();
//        p0.add("3");
//        p0.add("4");
//        p1.add("8");
//        params.add(p0);
//        params.add(p1);
        try{
            HashMap<String, Boolean>map = new HashMap<String, Boolean>();
            ZeebeClient zeebeClient = zeebeClientService.createClient(targetBorker);
            SysInstance sysInstance = instanceService.getInstanceByInstanceName(instanceName);
            String modelName = sysInstance.getModelName();
            String processId = sysInstance.getProcessId();
            List<TransferData> transferDatas = instanceService.changeSysInstancetoJobWorker(instanceName);
            boolean deployFlag = zeebeClientService.deploy(zeebeClient,modelName,processId);
            map.put("deployFlag",deployFlag);
            int i = 0;
            for(TransferData transferData:transferDatas) {
                String jobIdT = transferData.getJobId();
                String jobType = transferData.getJobType();
                String microserviceName = transferData.getMicroserviceName();
                List<String> tdatas = transferData.getTdatas();
                String url = zeebeClientService.transferParam(microserviceName, tdatas);
                boolean createWorkerFlag = zeebeClientService.createWorker(zeebeClient,jobIdT,jobType,url);
                map.put("createWorkerFlag", createWorkerFlag);
                i++;
            }
        return ResponseUtil.ok(map);
        }catch (Exception e){
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping(value = "/client")
    public ResponseJson clientRun(@RequestParam(value ="targetBorker")String targetBorker,
                                  @RequestParam(value ="modelName")String modelName,
                                  @RequestParam(value ="bpmnProcessId")String bpmnProcessId,
                                  @RequestParam(value ="jobTypes")List<String> jobTypes,
                                  @RequestParam(value ="microserviceName")List<String> microserviceNames){
        List<List<Integer>> params = new LinkedList<>();
        params.add(new LinkedList<>(Arrays.asList(1, 2)));
        params.add(new LinkedList<>(Arrays.asList(6)));
        try{
            HashMap<String, Boolean>map = new HashMap<String, Boolean>();
            final ZeebeClient client = ZeebeClient.newClientBuilder()
                    .brokerContactPoint(targetBorker)
                    .build();
            System.out.println("Connected.");

            final DeploymentEvent deployment = client.newDeployCommand()
                    .addResourceFromClasspath(modelService.getModelLocationByModelName(modelName))
                    .send()
                    .join();

            final int version = deployment.getWorkflows().get(0).getVersion();
            System.out.println("Workflow deployed. Version: " + version);

            final WorkflowInstanceEvent wfInstance = client.newCreateInstanceCommand()
                    .bpmnProcessId(bpmnProcessId)
                    .latestVersion()
                    //.variables()
                    .send()
                    .join();

            final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();

            System.out.println("Workflow instance created. Key: " + workflowInstanceKey);
            int i = 0;
            for(String jobType:jobTypes){
                String microserviceName = microserviceNames.get(i);
                List<Integer> param = params.get(i);
                System.out.println("param.size():"+param.size());
                final JobWorker jobWorker = client.newWorker()
                        .jobType(jobType)
                        .handler((jobClient, job) ->
                        {
                            String jobId =job.getElementId();
                            SysMicroservice sysMicroservice =sysMicroserviceMapper.selectMicroserviceByMicroserviceName(microserviceName);
                            String location = sysMicroservice.getMicroserviceLocation();
                            String temp = "?";
                            int a = 1;
                            for(int parami:param){
                                temp += "param" + a + "=" + parami + "&";
                                a++;
                            }
                            temp = temp.substring(0,temp.length()-1);
                            String url = location + temp;
//                            String url = "http://127.0.0.1:8088/test/add_8?a=1";
                            System.out.println("url:"+url);
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

                            System.out.println("task_");
                            jobClient.newCompleteCommand(job.getKey())
                                    .send()
                                    .join();
                        })
                        .open();
                i++;
            }
//            client.close();
//            System.out.println("Closed.");
            return ResponseUtil.ok();
        }catch (Exception e){
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/transferparam")
    public ResponseJson transferParam(String microserviceName) {

        List<String> param = new LinkedList<>();
        param.add("9");
        param.add("8");
        String url = zeebeClientService.transferParam(microserviceName,param);
        return ResponseUtil.ok(url);
    }

}
