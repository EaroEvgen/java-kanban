package ru.yandex.controllers;

import java.util.List;

import ru.yandex.task.*;

public interface TaskManager {
    public <T extends Task> int addTask (T task);
    public <T extends Task> void updateTask (T task);
    public void removeByID (int id);
    public void setTaskStatusByID (int id, TaskStatus status);
    public Task getTaskByID (int id);
    public void deleteTasks();
    public void deleteSubtasks();
    public void deleteEpics();
    public List<Task> getTaskList();
    public List<EpicTask> getEpicTaskList();
    public List<SubTask> getSubTaskList();
    public List<SubTask> getSubTaskList(int epicTaskID);
    public List<Task> getTaskHistory ();

}
