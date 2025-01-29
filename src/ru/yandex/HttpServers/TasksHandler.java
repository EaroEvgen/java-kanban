package ru.yandex.HttpServers;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.controllers.TaskManager;
import ru.yandex.task.CounterID;
import ru.yandex.task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Optional;

public class TasksHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK: {
                handleGetTaskByID(exchange);
                break;
            }
            case POST_CREATE: {
                handleCreateTask(exchange);
                break;
            }
            case POST_UPDATE: {
                handleUpdateTask(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if(taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        if (taskManager.getTaskByID(taskId) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
            return;
        }
        taskManager.removeByID(taskId);
        writeResponse(exchange, "Задача с ID: " + taskId + " удалена.", 200);
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if(taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        if (taskManager.getTaskByID(taskId) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
            return;
        }
        Optional<Task> curTask = getTaskByJson(exchange);
        if (curTask.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        taskManager.updateTask(curTask.get());
        writeResponse(exchange, "Задача с ID: " + taskId + " удалена.", 200);
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        Optional<Task> curTaskOptional = getTaskByJson(exchange);
        if (curTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        Task curTask = curTaskOptional.get();
        if (taskManager.getTaskByID(curTask.getId()) != null) {
            curTask.setId(CounterID.getNextID());
        }
        taskManager.addTask(curTask);
        writeResponse(exchange, "Задача с ID: " + curTask.getId() + " создана.", 200);
    }

    private void handleGetTaskByID(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if(taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        Task curTask = taskManager.getTaskByID(taskId);
        if (curTask == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
            return;
        }
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curTask), 200);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> curTaskList = taskManager.getTaskList();
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curTaskList), 200);
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private Optional<Task> getTaskByJson(HttpExchange exchange) throws IOException {
        Task curTask = gson.fromJson(Arrays.toString(exchange.getRequestBody().readAllBytes()), Task.class);
        if (curTask == null) {
            return Optional.empty();
        }
        return Optional.of(curTask);
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET": {
                if(Pattern.matches("^/tasks$", requestPath)) {
                    return Endpoint.GET_TASKS;
                } else if (Pattern.matches("^/tasks/\\d++$", requestPath)) {
                    return Endpoint.GET_TASK;
                }
                break;
            }
            case "POST": {
                if(Pattern.matches("^/tasks$", requestPath)) {
                    return Endpoint.POST_CREATE;
                } else if (Pattern.matches("^/tasks/\\d++$", requestPath)) {
                    return Endpoint.POST_UPDATE;
                }
                break;
            }
            case "DELETE": {
                if (Pattern.matches("^/tasks/\\d++$", requestPath)) {
                    return Endpoint.DELETE_TASK;
                }
                break;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_TASKS, GET_TASK, POST_CREATE, POST_UPDATE, DELETE_TASK, UNKNOWN}
}
