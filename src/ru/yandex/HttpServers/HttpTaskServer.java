package ru.yandex.HttpServers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.adapters.DurationTypeAdapter;
import ru.yandex.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.controllers.Managers;
import ru.yandex.controllers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private static HttpServer httpServer;

    private static TaskManager taskManager;
    private static Gson gson;

    public static void main(String[] args) throws IOException {
        taskManager = Managers.getDefault();
        gson = Managers.getDefaultGson();

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


