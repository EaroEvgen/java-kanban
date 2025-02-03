package ru.yandex.HttpServers;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.controllers.TaskManager;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.task.SubTask;
import ru.yandex.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Optional;

public class SubtasksHandler extends BaseTasksHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUB_TASKS: {
                handleGetSubTasks(exchange);
                break;
            }
            case GET_SUB_TASK: {
                handleGetSubTaskByID(exchange);
                break;
            }
            case POST_CREATE: {
                handleCreateSubTask(exchange);
                break;
            }
            case POST_UPDATE: {
                handleUpdateSubTask(exchange);
                break;
            }
            case DELETE_SUB_TASK: {
                handleDeleteSubTask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
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

    private void handleUpdateSubTask(HttpExchange exchange) throws IOException {
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
        Optional<SubTask> curSubTaskOptional = getSubTaskByJson(exchange);
        if (curSubTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        SubTask curSubTask = curSubTaskOptional.get();
        curSubTask.setId(taskId);
        taskManager.updateTask(curSubTask);
        writeResponse(exchange, "Задача с ID: " + taskId + " обновлена.", 200);
    }

    private void handleCreateSubTask(HttpExchange exchange) throws IOException {
        Optional<SubTask> curSubTaskOptional = getSubTaskByJson(exchange);
        if (curSubTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        SubTask curSubTask = curSubTaskOptional.get();
        if (taskManager.getTaskByID(curSubTask.getId()) != null) {
            writeResponse(exchange, "Задача с ID: " + curSubTask.getId() + " уже существует.", 400);
            return;
        }
        try {
            taskManager.addTaskException(curSubTask);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, "Не удалось добавить новую задачу. \n" + e.getMessage(), 400);
            return;
        }
        writeResponse(exchange, "Задача с ID: " + curSubTask.getId() + " создана.", 200);
    }

    private void handleGetSubTaskByID(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        Task curTask = taskManager.getTaskByID(taskId);
        if (curTask.getClass() != SubTask.class) {
            writeResponse(exchange, "Такой задачи нет", 404);
            return;
        }

        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curTask), 200);
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> curTaskList = taskManager.getSubTaskList();
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curTaskList), 200);
    }

    protected Optional<SubTask> getSubTaskByJson(HttpExchange exchange) throws IOException {
        // получаем входящий поток байтов
        InputStream inputStream = exchange.getRequestBody();
        // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        SubTask curSubTask;
        try {
            curSubTask = gson.fromJson(body, SubTask.class);
        } catch (Exception e) {
            return Optional.empty();
        }

        if (curSubTask == null) {
            return Optional.empty();
        }
        return Optional.of(curSubTask);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET": {
                if (Pattern.matches("^/subtasks$", requestPath)) {
                    return Endpoint.GET_SUB_TASKS;
                } else if (Pattern.matches("^/subtasks/\\d++$", requestPath)) {
                    return Endpoint.GET_SUB_TASK;
                }
                break;
            }
            case "POST": {
                if (Pattern.matches("^/subtasks$", requestPath)) {
                    return Endpoint.POST_CREATE;
                } else if (Pattern.matches("^/subtasks/\\d++$", requestPath)) {
                    return Endpoint.POST_UPDATE;
                }
                break;
            }
            case "DELETE": {
                if (Pattern.matches("^/subtasks/\\d++$", requestPath)) {
                    return Endpoint.DELETE_SUB_TASK;
                }
                break;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_SUB_TASKS, GET_SUB_TASK, POST_CREATE, POST_UPDATE, DELETE_SUB_TASK, UNKNOWN }
}