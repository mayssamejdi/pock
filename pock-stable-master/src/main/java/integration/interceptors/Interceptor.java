package integration.interceptors;

import integration.flowable_work.FlowableWork;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
@Component
public class Interceptor implements HandlerInterceptor{


    public String role;
    public String username;
    public String mail;
    public String token;
    public String method;
    public  ProcessEngine processEngine;
    public Map<String, Object> variables = new HashMap<String, Object>();
    public ProcessInstance processInstance;
    public RuntimeService runtimeService;

    public void initiateFlowable(){
        FlowableWork firstWork = new FlowableWork();
        firstWork.initiate();
        processEngine=firstWork.getProcessEngine();
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            this.initiateFlowable();
            String token0 =request.getHeader("Authorization").substring(7);
            token=token0.split("\\.")[1];
            String s=new String(Base64.decodeBase64(token),"UTF-8");
            JSONObject jsonObject = new JSONObject(s);
            JSONObject resourceAccess=(JSONObject) jsonObject.get("resource_access");
            JSONObject back=(JSONObject) resourceAccess.get("back");
            JSONArray role1=(JSONArray) back.get("roles");
            mail=(String) jsonObject.get("email");
            username=(String) jsonObject.get("preferred_username");
            role=(String) role1.get(0);
            System.out.println(role);
            method = request.getMethod();
            runtimeService = processEngine.getRuntimeService();
            variables.put("employee", username);
            variables.put("email", mail);
            variables.put("operation", method);
            processInstance = runtimeService.startProcessInstanceByKey("DeletionRequest", variables);
            TaskService taskService = processEngine.getTaskService();
            List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
            Task task = tasks.get(0);
            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            variables = new HashMap<String, Object>();
            HistoryService historyService = processEngine.getHistoryService();
            if (method.equals("DELETE") || method.equals("PUT")) {
                if (role.equals("admin")) {
                    boolean approved = true;
                  //  variables.put("approved", approved);
                   // taskService.complete(task.getId(), variables);
                } else {
                    boolean approved = false;
                  //  variables.put("approved", approved);
                   // taskService.complete(task.getId(), variables);
                    response.sendError(401);
                }
            }
        }catch (NullPointerException e){
            e.getStackTrace();
            System.out.println("header field must not be empty !!!");
        }
        return true;
    }
}