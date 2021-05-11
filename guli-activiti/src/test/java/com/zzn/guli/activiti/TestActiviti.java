package com.zzn.guli.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.AntPathMatcher;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestActiviti {
    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void initDeploymentBPMN(){
        String  filename="bpmn/test04.bpmn";
        Deployment deployment = repositoryService.createDeployment().addClasspathResource(filename)
                .name("test04").key("flow-04").deploy();
        System.out.println(deployment.getName());
    }

    @Test
    public void initDeploymentZIP() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/test02.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).name("test02-zip")
                .deploy();
        System.out.println(deploy.getName());
    }

    //查询流程部署
    @Test
    public void getDeployments() {
        List<Deployment> list = repositoryService.createDeploymentQuery()
//                .deploymentId("e04b7729-a0ec-11eb-a25b-00ff7719cb9a")
                .list();
        for(Deployment dep : list){
            System.out.println("------部署对象--------");
            System.out.println("Id："+dep.getId());
            System.out.println("Name："+dep.getName());
            System.out.println("DeploymentTime："+dep.getDeploymentTime());
            System.out.println("Key："+dep.getKey());
        }

    }

    //查询流程定义
    @Test
    public void getDefinitions(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
//                .deploymentId("e04b7729-a0ec-11eb-a25b-00ff7719cb9a")
                .list();
        for(ProcessDefinition pd : list){
            System.out.println("------流程定义--------");
            System.out.println("Name："+pd.getName());
            System.out.println("Key："+pd.getKey());
            System.out.println("id："+pd.getId());
            System.out.println("ResourceName："+pd.getResourceName());
            System.out.println("DeploymentId："+pd.getDeploymentId());
            System.out.println("Version："+pd.getVersion());
        }
    }

    @Autowired
    private RuntimeService runtimeService;

    @Test
    public void initProcessInstance(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess_2");
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());
    }

    //获取流程实例列表
    @Test
    public void getProcessInstances(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for(ProcessInstance pi : list){
            System.out.println("--------流程实例------");
            System.out.println("ProcessInstanceId："+pi.getProcessInstanceId());
            System.out.println("ProcessDefinitionId："+pi.getProcessDefinitionId());
            System.out.println("isEnded"+pi.isEnded());
            System.out.println("isSuspended："+pi.isSuspended());
        }
    }

    @Autowired
    private TaskService taskService;
    @Test
    public void getTasks(){
        List<Task> list = taskService.createTaskQuery().list();
        for(Task tk : list){
            System.out.println("Id："+tk.getId());
            System.out.println("Name："+tk.getName());
            System.out.println("Assignee："+tk.getAssignee());
        }
    }

    @Test
    public void getTasksByAssignee(){
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("wangpei").list();
        for (Task tk : tasks) {
            System.out.println("Id："+tk.getId());
            System.out.println("Name："+tk.getName());
            System.out.println("Assignee："+tk.getAssignee());
        }
    }

    @Test
    public void completeTask(){
        taskService.complete("c6d5099e-a183-11eb-9b5d-00ff7719cb9a");
        System.out.println("完成任务");
    }

    @Test
    public  void testAntPathMatch(){
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches("997601","$2a$10$WZ6AW.uzLdzxUeXvtZjBle.MKPnYNNn6Jez0y4U7hlYFYP1fEfbo." );
        System.out.println(matches);
    }
}
