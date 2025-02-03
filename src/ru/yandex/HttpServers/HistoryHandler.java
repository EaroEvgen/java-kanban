package ru.yandex.HttpServers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.controllers.TaskManager;
import ru.yandex.task.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseTasksHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY: {
                handleGetHistory(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> curTaskList = taskManager.getTaskHistory();
        Headers requestHeaders = exchange.getResponseHeaders();
        requestHeaders.add("Content-Type", "application/json");
        writeResponse(exchange, gson.toJson(curTaskList), 200);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET": {
                if (Pattern.matches("^/history$", requestPath)) {
                    return Endpoint.GET_HISTORY;
                }
                break;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_HISTORY, UNKNOWN }

}