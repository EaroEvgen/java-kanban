package ru.yandex.controllers;
import ru.yandex.task.*;

import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, EpicTask> epicTaskList;
    private final HashMap<Integer, SubTask> subTaskList;

    public TaskManager() {
        this.taskList = new HashMap<>();
        this.epicTaskList = new HashMap<>();
        this.subTaskList = new HashMap<>();
    }

    public void addTask (Task task) {
        if (task == null) {
            return;
        }
        taskList.put(task.getId(), task);
    }

    public void addTask (EpicTask task) {
        if (task == null) {
            return;
        }
        epicTaskList.put(task.getId(), task);
    }

    public void addTask (SubTask task) {
        if (task == null) {
            return;
        }
        if (task.getEpicTask() == null) {
            return;
        }
        subTaskList.put(task.getId(), task);
        task.getEpicTask().addSubTask(task);
    }

    public void updateTask (Task task) {
        if (task == null) {
            return;
        }
        if (!taskList.containsKey(task.getId())) {
            return;
        }
        taskList.remove(task.getId());
        taskList.put(task.getId(), task);
    }

    public void updateTask (EpicTask task) {
        if (task == null) {
            return;
        }
        if (!epicTaskList.containsKey(task.getId())) {
            return;
        }
        epicTaskList.remove(task.getId());
        epicTaskList.put(task.getId(), task);
    }

    public void updateTask (SubTask task) {
        if (task == null) {
            return;
        }
        if (!subTaskList.containsKey(task.getId())) {
            return;
        }
        SubTask currentSubTask = subTaskList.get(task.getId());
        EpicTask currentEpicTask = currentSubTask.getEpicTask();
        currentEpicTask.removeSubTask(currentSubTask);
        currentEpicTask.addSubTask(task);
        subTaskList.remove(task.getId());
        subTaskList.put(task.getId(), task);
    }

    public void removeByID (int id) {
        taskList.remove(id);
        epicTaskList.remove(id);
        if (subTaskList.containsKey(id)) {
            SubTask currentSubTask = subTaskList.get(id);
            currentSubTask.getEpicTask().removeSubTask(currentSubTask);
            subTaskList.remove(id);
        }
    }

    public void setTaskStatusByID (int id, TaskStatus status) {
        if (taskList.containsKey(id)) {
            taskList.get(id).setStatus(status);
        }
        if (epicTaskList.containsKey(id)) {
            epicTaskList.get(id).setStatus(status);
        }
        if (subTaskList.containsKey(id)) {
            subTaskList.get(id).setStatus(status);
        }
    }

    public Task getTaskByID (int id) {
        if (taskList.containsKey(id)) {
            return taskList.get(id);
        } else if (epicTaskList.containsKey(id)) {
            return epicTaskList.get(id);
        } else if (subTaskList.containsKey(id)) {
            return subTaskList.get(id);
        }
        return null;
    }

    public void deleteTasks() {
        taskList.clear();
    }

    public void deleteSubtasks() {
        for (EpicTask epic : epicTaskList.values()) {
            epic.cleanSubtaskIds();
        }
        subTaskList.clear();
    }

    public void deleteEpics() {
        subTaskList.clear();
        epicTaskList.clear();
    }

    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, EpicTask> getEpicTaskList() {
        return epicTaskList;
    }

    public HashMap<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }

    public HashMap<Integer, SubTask> getSubTaskList(int epicTaskID) {
        HashMap<Integer, SubTask> result = new HashMap<>();
        for (SubTask task : subTaskList.values()) {
            if (task.getEpicTask().getId() == (Integer)epicTaskID) {
                result.put(task.getId(), task);
            }
        }
        return result;
    }
}
