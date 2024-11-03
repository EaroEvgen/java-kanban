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
    public <T extends Task> int addTask (T task) {
        switch (task) {
            case null -> {
                return -1;
            }
            case EpicTask epicTask -> {
                epicTaskList.put(task.getId(), epicTask);
                return task.getId();
            }
            case SubTask subTask -> {
                if (subTask.getEpicTask() == null) {
                    return -2;
                }
                subTaskList.put(task.getId(), subTask);
                subTask.getEpicTask().addSubTask(subTask);
                return task.getId();
            }
            default -> {
                taskList.put(task.getId(), task);
                return task.getId();
            }
        }
    }

    @Override
    public <T extends Task> void updateTask (T task) {
        switch (task) {
            case null -> {
                return;
            }
            case EpicTask epicTask -> {
                if (!epicTaskList.containsKey(task.getId())) {
                    return;
                }
                epicTaskList.remove(task.getId());
                epicTaskList.put(task.getId(), epicTask);
                return;
            }
            case SubTask subTask -> {
                if (!subTaskList.containsKey(task.getId())) {
                    return;
                }
                SubTask currentSubTask = subTaskList.get(subTask.getId());
                EpicTask currentEpicTask = currentSubTask.getEpicTask();
                currentEpicTask.removeSubTask(currentSubTask);
                currentEpicTask.addSubTask(subTask);
                subTaskList.remove(subTask.getId());
                subTaskList.put(subTask.getId(), subTask);
                return;
            }
            default -> {
                if (!taskList.containsKey(task.getId())) {
                    return;
                }
                taskList.remove(task.getId());
                taskList.put(task.getId(), task);
                return;
            }
        }
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
        if (subTaskList.containsKey(id)) {
            subTaskList.get(id).setStatus(status);
            // статус эпика обновляет подзадача. Я посчитал
            // что так сложнее ошибиться будет и подзадача
            // имеет ссылку на свой эпик...
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
        historyManager.add(currentTask);
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
    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskList.values());
    }

    @Override
    public List<SubTask> getSubTaskList(int epicTaskID) {
        return epicTaskList.get(epicTaskID).getSubTaskList();
    }

    @Override
    public List<Task> getTaskHistory () {
        return historyManager.getHistory();
    }
}
