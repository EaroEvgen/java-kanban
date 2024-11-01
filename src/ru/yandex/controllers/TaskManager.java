package ru.yandex.controllers;

import java.util.List;

import ru.yandex.task.*;

public interface TaskManager {
    public int addTask (Task task);
    public int addTask (EpicTask task);
    public int addTask (SubTask task);
    public void updateTask (Task task);
    public void updateTask (EpicTask task);
    public void updateTask (SubTask task);
    public void removeByID (int id);
    public void setTaskStatusByID (int id, TaskStatus status);
    public Task getTaskByID (int id);
    public void deleteTasks();
    public void deleteSubtasks();
    public void deleteEpics();
    public List<Task> getTaskList();
    public List<EpicTask> getEpicTaskList();
    public List<Task> getSubTaskList();
    public List<SubTask> getSubTaskList(int epicTaskID);
    public List<Task> getTaskHistory ();

}
