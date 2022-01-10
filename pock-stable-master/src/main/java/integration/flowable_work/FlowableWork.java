package integration.flowable_work;

import integration.repository.RequestsRepository;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FlowableWork {

    @Autowired
    RequestsRepository requestsRepository;

    private ProcessEngine processEngine;
    private Scanner scanner = new Scanner(System.in);
    private Map<String, Object> variables = new HashMap<String, Object>();
    private RuntimeService runtimeService;
    private ProcessInstance processInstance;

    //instantiate the process engine
    public void initiate() {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = cfg.buildProcessEngine();

        //deploying the process
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("DeleteCategory.bpmn20.xml")
                .deploy();

        //test of deployment
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        System.out.println("Process named '"+ processDefinition.getName()+"' have been deployed successfuly " );
    }


    public void addUser() {
        System.out.println("Who are you ?");
        String employee = scanner.nextLine();
        System.out.println("type your Email ?");
        String email = scanner.nextLine();
        System.out.println("what is the operation to execute (delete/update)?");
        String operation = scanner.nextLine();

        runtimeService = processEngine.getRuntimeService();
        variables.put("employee", employee);
        variables.put("email", email);
        variables.put("operation", operation);

        processInstance = runtimeService.startProcessInstanceByKey("DeletionRequest", variables);
    }
    public void adminWork()  {
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ") " + tasks.get(i).getName());
        }

        System.out.println("Which task would you like to complete?");
        int taskIndex = Integer.valueOf(scanner.nextLine());
        Task task = tasks.get(taskIndex - 1);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println("someone  asks for " + processVariables.get("operation") + " permission : do you approve this?");

        boolean approved = scanner.nextLine().toLowerCase().equals("y");
        variables = new HashMap<String, Object>();
        HistoryService historyService = processEngine.getHistoryService();
        System.out.println(historyService);
        System.out.println(processInstance.getId());
        variables.put("approved", approved);
        taskService.complete(task.getId(), variables);
        runtimeService.suspendProcessInstanceById(processInstance.getId());
    }
    public ProcessEngine getProcessEngine(){
        return processEngine;
    }
}