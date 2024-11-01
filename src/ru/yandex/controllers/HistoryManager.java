package ru.yandex.controllers;
import ru.yandex.task.Task;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);
    public List<Task> getHistory();
}
