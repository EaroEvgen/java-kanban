package ru.yandex.controllers;

import ru.yandex.task.*;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, EpicTask> epicTaskList;
    private final HashMap<Integer, SubTask> subTaskList;
    private final HistoryManager historyManager;
    private Set<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        this.taskList = new HashMap<>();
        this.epicTaskList = new HashMap<>();
        this.subTaskList = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>((Task task1, Task task2) -> {
            if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;
            }
            return 1;
        });
    }

    @Override
    public <T extends Task> int addTask(T task) {
        if (!checkOverlapsInTime(task)) {
            System.out.println("Новая задача пересекается по времени с одной из существующих");
            return -4;
        }
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
                if (!prioritizedTasks.add(subTask)) {
                    System.out.println("Не удалось добавить в список со временем.");
                    return -3;
                }
                return task.getId();
            }
            default -> {
                taskList.put(task.getId(), task);
                if (!prioritizedTasks.add(task)) {
                    System.out.println("Не удалось добавить в список со временем.");
                    return -3;
                }
                return task.getId();
            }
        }
    }

    @Override
    public <T extends Task> void updateTask(T task) {
        if (!checkOverlapsInTime(task)) {
            System.out.println("Новая задача пересекается по времени с одной из существующих");
            return;
        }
        switch (task) {
            case null -> {
            }
            case EpicTask epicTask -> {
                if (!epicTaskList.containsKey(task.getId())) {
                    return;
                }
                epicTaskList.remove(task.getId());
                epicTaskList.put(task.getId(), epicTask);
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
                prioritizedTasks.remove(subTask);
                if (!prioritizedTasks.add(subTask)) {
                    System.out.println("Не удалось добавить в список со временем.");
                }
            }
            default -> {
                if (!taskList.containsKey(task.getId())) {
                    return;
                }
                taskList.remove(task.getId());
                taskList.put(task.getId(), task);
                prioritizedTasks.remove(task);
                if (!prioritizedTasks.add(task)) {
                    System.out.println("Не удалось добавить в список со временем.");
                }
            }
        }
    }

    @Override
    public void removeByID(int id) {
        prioritizedTasks.remove(getTaskByID(id));
        taskList.remove(id);
        epicTaskList.remove(id);
        if (subTaskList.containsKey(id)) {
            SubTask currentSubTask = subTaskList.get(id);
            currentSubTask.getEpicTask().removeSubTask(currentSubTask);
            subTaskList.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public void setTaskStatusByID(int id, TaskStatus status) {
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
    public Task getTaskByID(int id) {
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
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> task.getClass() != Task.class)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteSubtasks() {
        for (EpicTask epic : epicTaskList.values()) {
            epic.cleanSubtaskIds();
        }
        subTaskList.clear();
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> task.getClass() != SubTask.class)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteEpics() {
        subTaskList.clear();
        epicTaskList.clear();
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> task.getClass() != EpicTask.class)
                .filter(task -> task.getClass() != SubTask.class)
                .collect(Collectors.toSet());
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
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public static boolean isOverlapsInTime(Task task1, Task task2) {
        if (task1.getStartTime().isBefore(task2.getStartTime())
                && task1.getEndTime().isBefore(task2.getStartTime())) {
            return false;
        }
        if (task1.getStartTime().isAfter(task2.getEndTime())
                && task1.getEndTime().isAfter(task2.getEndTime())) {
            return false;
        }
        return true;
    }

    public boolean checkOverlapsInTime(Task task) {
        return prioritizedTasks.stream()
                .filter(curTask -> isOverlapsInTime(curTask, task))
                .toList().isEmpty();
    }
}
