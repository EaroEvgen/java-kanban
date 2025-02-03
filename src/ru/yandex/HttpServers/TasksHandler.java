package ru.yandex.HttpServers;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.controllers.TaskManager;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.task.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Optional;

public class TasksHandler extends BaseTasksHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
        if (taskIdOptional.isEmpty()) {
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
        if (taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        if (taskManager.getTaskByID(taskId) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
            return;
        }
        Optional<Task> curTaskOptional = getTaskByJson(exchange);
        if (curTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        Task curTask = curTaskOptional.get();
        curTask.setId(taskId);
        taskManager.updateTask(curTask);
        writeResponse(exchange, "Задача с ID: " + taskId + " обновлена.", 200);
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        Optional<Task> curTaskOptional = getTaskByJson(exchange);
        if (curTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        Task curTask = curTaskOptional.get();
        if (taskManager.getTaskByID(curTask.getId()) != null) {
            writeResponse(exchange, "Задача с ID: " + curTask.getId() + " уже существует.", 400);
            return;
        }
        try {
            taskManager.addTaskException(curTask);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, "Не удалось добавить новую задачу. \n" + e.getMessage(), 400);
            return;
        }
        writeResponse(exchange, "Задача с ID: " + curTask.getId() + " создана.", 200);
    }

    private void handleGetTaskByID(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
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

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET": {
                if (Pattern.matches("^/tasks$", requestPath)) {
                    return Endpoint.GET_TASKS;
                } else if (Pattern.matches("^/tasks/\\d++$", requestPath)) {
                    return Endpoint.GET_TASK;
                }
                break;
            }
            case "POST": {
                if (Pattern.matches("^/tasks$", requestPath)) {
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

    enum Endpoint { GET_TASKS, GET_TASK, POST_CREATE, POST_UPDATE, DELETE_TASK, UNKNOWN }
}