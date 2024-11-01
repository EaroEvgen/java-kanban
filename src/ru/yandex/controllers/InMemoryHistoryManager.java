package ru.yandex.controllers;

import ru.yandex.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistory;

    private final static int MAX_HISTORY_COUNT = 10;

    public InMemoryHistoryManager() {
        this.taskHistory = new ArrayList<>();
    }

    public void add(Task task) {
        if (taskHistory.size() >= MAX_HISTORY_COUNT) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }
    public List<Task> getHistory() {
        return this.taskHistory;
    }
}
