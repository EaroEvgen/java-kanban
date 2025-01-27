package ru.yandex.controllers;

import java.util.List;

import ru.yandex.task.*;

public interface TaskManager {
    <T extends Task> int addTask(T task);

    <T extends Task> void updateTask(T task);

    void removeByID(int id);

    void setTaskStatusByID(int id, TaskStatus status);

    Task getTaskByID(int id);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    List<Task> getTaskList();

    List<EpicTask> getEpicTaskList();

    List<SubTask> getSubTaskList();

    List<SubTask> getSubTaskList(int epicTaskID);

    List<Task> getTaskHistory();

    List<Task> getPrioritizedTasks();

}
