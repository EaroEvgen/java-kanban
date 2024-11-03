package ru.yandex.controllers;
import ru.yandex.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
