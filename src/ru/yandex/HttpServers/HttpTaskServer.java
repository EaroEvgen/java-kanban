package ru.yandex.HttpServers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.controllers.Managers;
import ru.yandex.controllers.TaskManager;
import ru.yandex.task.EpicTask;
import ru.yandex.task.SubTask;
import ru.yandex.task.Task;
import ru.yandex.task.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private static HttpServer httpServer;

    private static TaskManager taskManager;
    private static Gson gson;

    public static void main(String[] args) throws IOException {
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        Task task = new Task("Task name", "Task description", TaskStatus.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        EpicTask epicTask = new EpicTask("Epic task name", "Epic task description");
        SubTask subTask = new SubTask("subtask name", "subtask description", epicTask, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));

        taskManager.addTask(task);
        taskManager.addTask(epicTask);
        taskManager.addTask(subTask);

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));

        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

        // завершаем работу сервера
        //httpServer.stop(1);
    }

    public Gson getGson() {
        return gson;
    }

    public void start() {
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));

            httpServer.start(); // запускаем сервер
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            System.out.println("HTTP-сервер не запущен");
        }
    }

    public void stop() {
        httpServer.stop(1);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSSSSSSSS");

    @Override
    public void write(JsonWriter jsonWriter, final LocalDateTime localTime) throws IOException {
        jsonWriter.value(localTime.format(timeFormatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
    }
}

class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.getSeconds());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.ofSeconds(Integer.parseInt(jsonReader.nextString()));
    }
}
