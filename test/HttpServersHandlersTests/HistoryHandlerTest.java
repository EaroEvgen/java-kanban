package HttpServersHandlersTests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.HttpServers.HttpTaskServer;
import ru.yandex.controllers.TaskManager;
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

public class HistoryHandlerTest {
    protected static HttpTaskServer taskServer;

    static class SubtitleListTypeToken extends TypeToken<List<Task>> {

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
    void checkEndpointGET_HISTORY() throws IOException, InterruptedException {
        Task task = new Task("Task name", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        List<Task> startTaskList = new ArrayList<>();
        startTaskList.add(task);

        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        taskManager.addTask(task);
        taskManager.getTaskByID(task.getId());

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");

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
        List<Task> responseTaskList = gson.fromJson(jsonBody, new TaskHandlerTest.SubtitleListTypeToken().getType());


        Assertions.assertTrue(startTaskList.containsAll(responseTaskList)
                && responseTaskList.containsAll(startTaskList));
    }
}
