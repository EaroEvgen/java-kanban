package ru.yandex.controllers;

import ru.yandex.exceptions.ManagerSaveException;
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
        try {
            return addTaskException(task);
        } catch (ManagerSaveException e) {
            //System.out.println(e.getMessage());
            return -1;
        }
    }

    public <T extends Task> int addTaskException(T task) throws ManagerSaveException {
        if (!checkOverlapsInTime(task)) {
            throw new ManagerSaveException("Новая задача пересекается по времени с одной из существующих");
        }
        switch (task) {
            case EpicTask epicTask -> {
                epicTaskList.put(task.getId(), epicTask);
                return task.getId();
            }
            case SubTask subTask -> {
                if (subTask.getEpicTask() == null) {
                    throw new ManagerSaveException("Новая подзадача имеет пустой поле эпической задачи");
                }
                subTaskList.put(task.getId(), subTask);
                subTask.getEpicTask().addSubTask(subTask);
                EpicTask.update(epicTaskList.get(subTask.getEpicTask().getId()), subTaskList.values().stream().toList());
                if (!prioritizedTasks.add(subTask)) {
                    throw new ManagerSaveException("Новая подзадача пересекается по времени с одной из существующих");
                }
                return task.getId();
            }
            default -> {
                taskList.put(task.getId(), task);
                if (!prioritizedTasks.add(task)) {
                    throw new ManagerSaveException("Не удалось добавить в список со временем.");
                }
                return task.getId();
            }
        }
    }

    @Override
    public <T extends Task> void updateTask(T task) {
        Task oldTask = getTaskByID(task.getId());
        removeByID(task.getId());
        if (!checkOverlapsInTime(task)) {
            System.out.println("Новая задача пересекается по времени с одной из существующих");
            addTask(oldTask);
            return;
        }
        addTask(task);
    }

    @Override
    public void removeByID(int id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
        taskList.remove(id);
        if (epicTaskList.containsKey(id)) {
            for (int subtaskId : epicTaskList.get(id).getSubTaskList()) {
                removeByID(subtaskId);
            }
        }
        epicTaskList.remove(id);
        if (subTaskList.containsKey(id)) {
            SubTask currentSubTask = subTaskList.get(id);
            EpicTask currentEpicTask = epicTaskList.get(currentSubTask.getEpicTask().getId());
            currentSubTask.getEpicTask().removeSubTask(currentSubTask);
            EpicTask.update(currentEpicTask, subTaskList.values().stream().toList());
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
            EpicTask.updateStatus(epicTaskList.get(subTaskList.get(id).getEpicTask().getId()),
                    subTaskList.values().stream().toList());
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
            EpicTask.updateStatus(epic, subTaskList.values().stream().toList());
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
        return EpicTask.getSubTaskList(epicTaskList.get(epicTaskID), subTaskList.values().stream().toList());
    }

    @Override
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public static boolean isOverlapsInTime(Task task1, Task task2) {
        if (task1.getEndTime().isBefore(task2.getStartTime())) {
            return false;
        }
        if (task1.getStartTime().isAfter(task2.getEndTime())) {
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
