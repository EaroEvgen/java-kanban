package ru.yandex.HttpServers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.controllers.Managers;
import ru.yandex.controllers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private static HttpServer httpServer;

    private static TaskManager taskManager = Managers.getDefault();
    private static Gson gson = new GsonBuilder().create();

    public static void main(String[] args) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
//        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
//        httpServer.createContext("/epics", new EpicsHandler(taskManager));
//        httpServer.createContext("/history", new HistoryHandler(taskManager));
//        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

        // завершаем работу сервера
        //httpServer.stop(1);
    }

    public void start() {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
//        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
//        httpServer.createContext("/epics", new EpicsHandler(taskManager));
//        httpServer.createContext("/history", new HistoryHandler(taskManager));
//        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

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
