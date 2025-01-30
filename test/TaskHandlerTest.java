import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.HttpServers.HttpTaskServer;
import ru.yandex.controllers.InMemoryTaskManager;
import ru.yandex.controllers.Managers;
import ru.yandex.controllers.TaskManager;
import ru.yandex.task.EpicTask;
import ru.yandex.task.SubTask;
import ru.yandex.task.Task;
import ru.yandex.task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskHandlerTest {

    protected static HttpTaskServer taskServer;

    class SubtitleListTypeToken extends TypeToken<List<Task>> {

    }

    @BeforeEach
    public void initTaskServer() {
        taskServer = new HttpTaskServer();
        taskServer.start();
    }

    @AfterEach
    public void deinit() {
        taskServer.stop();
    }

    @Test
    void checkEndpointGET_TASKS() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
        int taskId = task.getId();
        int epicTaskId = epicTask.getId();
        int subTaskId = subTask.getId();

        List<Task> startTaskList = new ArrayList<>();
        startTaskList.add(task);

        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks") ;

        // создайте объект, описывающий HTTP-запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .GET()
                .build();

        // отправьте запрос
        HttpResponse<String> response = httpClient.send(request, handler);
        Assertions.assertEquals(200, response.statusCode());

        String jsonBody = response.body();
        List<Task> responseTaskList = gson.fromJson(jsonBody, new SubtitleListTypeToken().getType());


        Assertions.assertTrue(startTaskList.containsAll(responseTaskList)
                && responseTaskList.containsAll(startTaskList));
    }

    @Test
    void checkEndpointGET_TASK() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
        int taskId = task.getId();
        int epicTaskId = epicTask.getId();
        int subTaskId = subTask.getId();

        List<Task> startTaskList = new ArrayList<>();
        startTaskList.add(task);
        startTaskList.add(epicTask);
        startTaskList.add(subTask);

        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId) ;

        // создайте объект, описывающий HTTP-запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .GET()
                .build();

        // отправьте запрос
        HttpResponse<String> response = httpClient.send(request, handler);
        Assertions.assertEquals(200, response.statusCode());

        String jsonBody = response.body();
        Task responseTask = gson.fromJson(jsonBody, Task.class);


        Assertions.assertEquals(responseTask, task);
    }

//    @Test
//    void addNewTask() {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
//        int taskId = task.getId();
//        int epicTaskId = epicTask.getId();
//        int subTaskId = subTask.getId();
//
//        HttpClient httpClient = HttpClient.newHttpClient();
//        URI uri = URI.create("http://localhost:8080/tasks") ;
//
//        // создайте объект, описывающий HTTP-запрос
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Content-type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
//                .build();
//
//
//
//        // отправьте запрос
//        try {
//            HttpResponse<String> response = httpClient.send(request, handler);
//        } catch (Exception e) {
//            System.out.println("Ошибка клиента при попытке отправить запрос.");
//        }
//    }

//    @Test
//    void safeTaskAllTypes () {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
//
//        int taskId = taskManager.addTask(task);
//        int epicTaskId = taskManager.addTask(epicTask);
//        int subTaskId = taskManager.addTask(subTask);
//
//        Assertions.assertEquals(task, taskManager.getTaskByID(taskId), "Не смог добавить простую задачу");
//        Assertions.assertEquals(epicTask, taskManager.getTaskByID(epicTaskId), "Не смог добавить эпическую задачу");
//        Assertions.assertEquals(subTask, taskManager.getTaskByID(subTaskId), "Не смог добавить подзадачу");
//    }
//
//    @Test
//    void methodGetAllTaskList() {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//
//        taskManager.addTask(task);
//        taskManager.addTask(epicTask);
//        taskManager.addTask(subTask);
//        List<Task> taskList = new ArrayList<>();
//        taskList.add(task);
//        List<EpicTask> epicTaskList = new ArrayList<>();
//        epicTaskList.add(epicTask);
//        List<SubTask> subTaskList = new ArrayList<>();
//        subTaskList.add(subTask);
//
//        Assertions.assertTrue(taskList.containsAll(taskManager.getTaskList())
//                && taskManager.getTaskList().containsAll(taskList));
//        Assertions.assertTrue(epicTaskList.containsAll(taskManager.getEpicTaskList())
//                && taskManager.getEpicTaskList().containsAll(epicTaskList));
//        Assertions.assertTrue(subTaskList.containsAll(taskManager.getSubTaskList())
//                && taskManager.getSubTaskList().containsAll(subTaskList));
//
//    }
//
//    @Test
//    void methodClearList() {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//
//        taskManager.addTask(task);
//        taskManager.addTask(epicTask);
//        taskManager.addTask(subTask);
//        taskManager.deleteTasks();
//        taskManager.deleteEpics();
//        taskManager.deleteSubtasks();
//
//        Assertions.assertTrue(taskManager.getTaskList().isEmpty());
//        Assertions.assertTrue(taskManager.getSubTaskList().isEmpty());
//        Assertions.assertTrue(taskManager.getEpicTaskList().isEmpty());
//
//    }
//
//    @Test
//    void methodUpdateTask () {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
//
//        int taskId = taskManager.addTask(task);
//        int epicTaskId = taskManager.addTask(epicTask);
//        int subTaskId = taskManager.addTask(subTask);
//
//        task = new Task("NEW Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(3), Duration.ofMinutes(10));
//        task.setId(taskId);
//        epicTask = new  EpicTask("NEW Epik task name", "Epik task description");
//        epicTask.setId(epicTaskId);
//        subTask = new SubTask("NEW subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(4), Duration.ofMinutes(10));
//        subTask.setId(subTaskId);
//
//        taskManager.updateTask(task);
//        taskManager.updateTask(epicTask);
//        taskManager.updateTask(subTask);
//
//        Assertions.assertEquals(task, taskManager.getTaskByID(taskId), "Не смог обновить простую задачу");
//        Assertions.assertEquals(epicTask, taskManager.getTaskByID(epicTaskId), "Не смог обновить эпическую задачу");
//        Assertions.assertEquals(subTask, taskManager.getTaskByID(subTaskId), "Не смог обновить подзадачу");
//
//    }
//
//    @Test
//    void methodRemoveByID () {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
//
//        int taskId = taskManager.addTask(task);
//        int epicTaskId = taskManager.addTask(epicTask);
//        int subTaskId = taskManager.addTask(subTask);
//        taskManager.removeByID(taskId);
//        taskManager.removeByID(epicTaskId);
//        taskManager.removeByID(subTaskId);
//        Assertions.assertNull(taskManager.getTaskByID(taskId), "Задача не удаляется по ИД");
//        Assertions.assertNull(taskManager.getTaskByID(epicTaskId), "Эпическая задача не удаляется по ИД");
//        Assertions.assertNull(taskManager.getTaskByID(subTaskId), "Подзадача не удаляется по ИД");
//    }
//
//    @Test
//    void getSubtaskInEpictask () {
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//
//        int epicTaskId = taskManager.addTask(epicTask);
//        taskManager.addTask(subTask);
//
//        List<SubTask> subTaskList = new ArrayList<>();
//        subTaskList.add(subTask);
//
//        Assertions.assertTrue(subTaskList.containsAll(taskManager.getSubTaskList(epicTaskId))
//                && taskManager.getSubTaskList(epicTaskId).containsAll(subTaskList));
//
//    }
//
//    @Test
//    void checkChangeStatus () {
//        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
//        EpicTask epicTask = new  EpicTask("Epik task name", "Epik task description");
//        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
//        SubTask subTaskNew = new SubTask("new subtask name", "new subtask description", epicTask, LocalDateTime.now().plusHours(3), Duration.ofMinutes(10));
//
//        int taskId = taskManager.addTask(task);
//        int epicTaskId = taskManager.addTask(epicTask);
//        int subTaskId = taskManager.addTask(subTask);
//
//        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(taskId).getStatus(), "Ошибки в работе со статусами");
//        taskManager.setTaskStatusByID(taskId, TaskStatus.IN_PROGRESS);
//        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByID(taskId).getStatus(), "Ошибки в работе со статусами");
//
//        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(epicTaskId).getStatus());
//        taskManager.setTaskStatusByID(epicTaskId, TaskStatus.IN_PROGRESS);
//        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");
//
//        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(subTaskId).getStatus());
//        taskManager.setTaskStatusByID(subTaskId, TaskStatus.DONE);
//        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskByID(subTaskId).getStatus(), "Ошибки в работе со статусами");
//        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");
//
//        int subTaskNewId = taskManager.addTask(subTaskNew);
//        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTaskByID(subTaskNewId).getStatus(), "Ошибки в работе со статусами");
//        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");
//        taskManager.setTaskStatusByID(subTaskNewId, TaskStatus.DONE);
//        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskByID(epicTaskId).getStatus(), "Ошибки в работе со статусами");
//    }
//
//    @Test
//    void checkDurationInEpicTask () {
//        LocalDateTime nowTime = LocalDateTime.now().plusHours(1);
//        EpicTask epic = new EpicTask(
//                "nameepic",
//                "descepic"
//        );
//        SubTask sub1 = new SubTask(
//                "subname1",
//                "subdesc1",
//                epic,
//                nowTime,
//                Duration.ofMinutes(10)
//        );
//        SubTask sub2 = new SubTask(
//                "subname2",
//                "subdesc2",
//                epic,
//                nowTime.plus(Duration.ofMinutes(11)),
//                Duration.ofMinutes(10)
//        );
//
//        int id = taskManager.addTask(epic);
//        taskManager.addTask(sub1);
//        taskManager.addTask(sub2);
//
//        Assertions.assertEquals(taskManager.getTaskByID(id).getDuration(), Duration.ofMinutes(20));
//    }
//
//    @Test
//    void checkFGetPrioritizedTasks() {
//        TaskManager tm = Managers.getDefault();
//        Random random = new Random();
//        int countData = 10;
//        for (int i = 0; i < countData; i++) {
//            Task curTask = new Task("Name task " + i + ".", "Description task", LocalDateTime.now().plusHours(random.nextInt(countData)), Duration.ofMinutes(10));
//            tm.addTask(curTask);
//        }
//
//        System.out.println(tm.getPrioritizedTasks());
//    }
}
