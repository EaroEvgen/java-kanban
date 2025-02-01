package HttpServersHandlersTests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.HttpServers.HttpTaskServer;
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

public class SubtasksHandlerTest {

    protected static HttpTaskServer taskServer;

    static class SubtitleListTypeToken extends TypeToken<List<SubTask>> {

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
    void checkEndpointGET_SUB_TASKS() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("Epic task name", "Epic task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));

        List<SubTask> startSubTaskList = new ArrayList<>();
        startSubTaskList.add(subTask);

        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");

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
        List<SubTask> responseTaskList = gson.fromJson(jsonBody, new SubtitleListTypeToken().getType());


        Assertions.assertTrue(startSubTaskList.containsAll(responseTaskList)
                && responseTaskList.containsAll(startSubTaskList));
    }

    @Test
    void checkEndpointGET_TASK() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        int taskId = task.getId();

        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        taskManager.addTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);

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

    @Test
    void checkEndpointPOST_CREATE() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        int taskId = task.getId();

        Gson gson = taskServer.getGson();
        TaskManager taskManager = taskServer.getTaskManager();
        String requestJson = gson.toJson(task);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");

        // создайте объект, описывающий HTTP-запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        // отправьте запрос
        HttpResponse<String> response = httpClient.send(request, handler);
        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals(taskManager.getTaskByID(taskId), task);
    }

    @Test
    void checkEndpointPOST_UPDATE() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task newTask = new Task("Task name new", "Test addNewTask description new", TaskStatus.DONE, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        int taskId = task.getId();
        newTask.setId(taskId);

        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        taskManager.addTask(task);
        String requestJson = gson.toJson(newTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);

        // создайте объект, описывающий HTTP-запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        // отправьте запрос
        HttpResponse<String> response = httpClient.send(request, handler);
        Assertions.assertEquals(200, response.statusCode());

        Task responseTask = taskManager.getTaskByID(taskId);
        Assertions.assertEquals(responseTask, newTask);
        Assertions.assertEquals(responseTask.getStatus(), newTask.getStatus());
        Assertions.assertEquals(responseTask.getName(), newTask.getName());
        Assertions.assertEquals(responseTask.getDescription(), newTask.getDescription());
        Assertions.assertEquals(responseTask.getStartTime(), newTask.getStartTime());
        Assertions.assertEquals(responseTask.getDuration(), newTask.getDuration());


    }

    @Test
    void checkEndpointDELETE_TASK() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        int taskId = task.getId();

        TaskManager taskManager = taskServer.getTaskManager();
        taskManager.addTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);

        // создайте объект, описывающий HTTP-запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .DELETE()
                .build();

        // отправьте запрос
        HttpResponse<String> response = httpClient.send(request, handler);
        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertNull(taskManager.getTaskByID(taskId));
    }
}
