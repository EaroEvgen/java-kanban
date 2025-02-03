package ru.yandex.HttpServers;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.controllers.TaskManager;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.task.EpicTask;
import ru.yandex.task.SubTask;
import ru.yandex.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Optional;

public class EpicsHandler extends BaseTasksHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPIC_TASKS: {
                handleGetEpicTasks(exchange);
                break;
            }
            case GET_EPIC_TASK: {
                handleGetEpicTaskByID(exchange);
                break;
            }
            case GET_EPIC_TASK_SUBTASK: {
                handleGetEpicTaskSubtasksByID(exchange);
                break;
            }
            case POST_CREATE: {
                handleCreateEpicTask(exchange);
                break;
            }
            case POST_UPDATE: {
                handleUpdateEpicTask(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteEpicTask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleDeleteEpicTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор эпической задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        if (taskManager.getTaskByID(taskId) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
            return;
        }
        taskManager.removeByID(taskId);
        writeResponse(exchange, "Эпическая задача с ID: " + taskId + " удалена.", 200);
    }

    private void handleUpdateEpicTask(HttpExchange exchange) throws IOException {
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
        Optional<EpicTask> curTaskOptional = getEpicTaskByJson(exchange);
        if (curTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        EpicTask curTask = curTaskOptional.get();
        curTask.setId(taskId);
        taskManager.updateTask(curTask);
        writeResponse(exchange, "Задача с ID: " + taskId + " обновлена.", 200);
    }

    private void handleCreateEpicTask(HttpExchange exchange) throws IOException {
        Optional<EpicTask> curTaskOptional = getEpicTaskByJson(exchange);
        if (curTaskOptional.isEmpty()) {
            writeResponse(exchange, "В теле некорректно заполнена задача", 404);
            return;
        }
        EpicTask curTask = curTaskOptional.get();
        if (taskManager.getTaskByID(curTask.getId()) != null) {
            writeResponse(exchange, "Задача с ID: " + curTask.getId() + " уже существует.", 400);
            return;
        }
        try {
            taskManager.addTaskException(curTask);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, "Не удалось добавить новую эпическую задачу. \n" + e.getMessage(), 400);
            return;
        }
        writeResponse(exchange, "Эптческая задача с ID: " + curTask.getId() + " создана.", 200);
    }

    private void handleGetEpicTaskByID(HttpExchange exchange) throws IOException {
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
        if (curTask.getClass() != EpicTask.class) {
            writeResponse(exchange, "Такой эпической задачи нет", 404);
            return;
        }
        EpicTask curEpicTask = (EpicTask) curTask;
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curEpicTask), 200);
    }

    private void handleGetEpicTasks(HttpExchange exchange) throws IOException {
        List<EpicTask> curTaskList = taskManager.getEpicTaskList();
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curTaskList), 200);
    }

    private void handleGetEpicTaskSubtasksByID(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOptional.get();
        Task curTask = taskManager.getTaskByID(taskId);
        if (curTask == null) {
            writeResponse(exchange, "Такой эпической задачи нет", 404);
            return;
        }
        if (curTask.getClass() != EpicTask.class) {
            writeResponse(exchange, "Такой эпической задачи нет", 404);
            return;
        }
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        EpicTask curEpicTask = (EpicTask) curTask;
        List<SubTask> subTaskList = EpicTask.getSubTaskList(curEpicTask, taskManager.getSubTaskList());
        writeResponse(exchange, gson.toJson(subTaskList), 200);
    }

    private Optional<EpicTask> getEpicTaskByJson(HttpExchange exchange) throws IOException {
        // получаем входящий поток байтов
        InputStream inputStream = exchange.getRequestBody();
        // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        EpicTask curTask;
        try {
            curTask = gson.fromJson(body, EpicTask.class);
        } catch (Exception e) {
            return Optional.empty();
        }

        if (curTask == null) {
            return Optional.empty();
        }
        return Optional.of(curTask);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET": {
                if (Pattern.matches("^/epics$", requestPath)) {
                    return Endpoint.GET_EPIC_TASKS;
                } else if (Pattern.matches("^/epics/\\d++$", requestPath)) {
                    return Endpoint.GET_EPIC_TASK;
                } else if (Pattern.matches("^/epics/\\d++/subtasks$", requestPath)) {
                    return Endpoint.GET_EPIC_TASK_SUBTASK;
                }
                break;
            }
            case "POST": {
                if (Pattern.matches("^/epics$", requestPath)) {
                    return Endpoint.POST_CREATE;
                } else if (Pattern.matches("^/epics/\\d++$", requestPath)) {
                    return Endpoint.POST_UPDATE;
                }
                break;
            }
            case "DELETE": {
                if (Pattern.matches("^/epics/\\d++$", requestPath)) {
                    return Endpoint.DELETE_TASK;
                }
                break;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_EPIC_TASKS, GET_EPIC_TASK, GET_EPIC_TASK_SUBTASK, POST_CREATE, POST_UPDATE, DELETE_TASK, UNKNOWN }
}