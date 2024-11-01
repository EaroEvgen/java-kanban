package ru.yandex.controllers;
import ru.yandex.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, EpicTask> epicTaskList;
    private final HashMap<Integer, SubTask> subTaskList;
    private final HistoryManager historyManager;


    public InMemoryTaskManager() {
        this.taskList = new HashMap<>();
        this.epicTaskList = new HashMap<>();
        this.subTaskList = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int addTask (Task task) {
        if (task == null) {
            return -1;
        }
        taskList.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addTask (EpicTask task) {
        if (task == null) {
            return -1;
        }
        epicTaskList.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addTask (SubTask task) {
        if (task == null) {
            return -1;
        }
        if (task.getEpicTask() == null) {
            return -2;
        }
        subTaskList.put(task.getId(), task);
        task.getEpicTask().addSubTask(task);
        return task.getId();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void removeByID (int id) {
        taskList.remove(id);
        epicTaskList.remove(id);
        if (subTaskList.containsKey(id)) {
            SubTask currentSubTask = subTaskList.get(id);
            currentSubTask.getEpicTask().removeSubTask(currentSubTask);
            subTaskList.remove(id);
        }
    }

    @Override
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

    @Override
    public Task getTaskByID (int id) {
        Task currentTask = null;
        if (taskList.containsKey(id)) {
            currentTask = taskList.get(id);
        } else if (epicTaskList.containsKey(id)) {
            currentTask = epicTaskList.get(id);
        } else if (subTaskList.containsKey(id)) {
            currentTask = subTaskList.get(id);
        }
        setTaskHistory(currentTask);
        return currentTask;
    }

    @Override
    public void deleteTasks() {
        taskList.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (EpicTask epic : epicTaskList.values()) {
            epic.cleanSubtaskIds();
        }
        subTaskList.clear();
    }

    @Override
    public void deleteEpics() {
        subTaskList.clear();
        epicTaskList.clear();
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<EpicTask> getEpicTaskList() {
        return new ArrayList<>(epicTaskList.values());
    }

    @Override
    public List<Task> getSubTaskList() {
        return new ArrayList<>(subTaskList.values());
    }

    @Override
    public List<SubTask> getSubTaskList(int epicTaskID) {
        return ((EpicTask) getTaskByID(epicTaskID)).getSubTaskList();
    }

    private void setTaskHistory (Task task) {
        historyManager.add(task);
    }

    @Override
    public List<Task> getTaskHistory () {
        return historyManager.getHistory();
    }
}
