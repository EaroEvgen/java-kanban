package ru.yandex.task;
import java.util.ArrayList;

public class TaskManager {
    private final ArrayList<Task> taskList;
    private int taskIdCounter;

    public TaskManager() {
        this.taskIdCounter = 0;
        this.taskList = new ArrayList<>();
    }

    public void addTask (Task task) {
        taskList.add(task);
    }

    public void addTask (String name, String description) {
        while (getTaskByID(taskIdCounter) != null) {
            taskIdCounter++;
        }
        taskList.add(new Task(taskIdCounter, name, description));
    }

    public void addTask (String name, String description, Task task) {
        while (getTaskByID(taskIdCounter) != null) {
            taskIdCounter++;
        }
        taskList.add(new Task(taskIdCounter, name, description, task));
    }

    public void addTask (String name, String description, int parentID) {
        while (getTaskByID(taskIdCounter) != null) {
            taskIdCounter++;
        }
        taskList.add(new Task(taskIdCounter, name, description, getTaskByID(parentID)));
    }

    public void updateTask (Task task) {
        if (task == null) {
            return;
        }
        taskList.remove(getTaskByID(task.getId()));
        taskList.add(task);
        if (task.getParent() == null) {
            setStatusParent(task);
        }
    }

    public void clearAllTask () {
        taskList.clear();
    }

    public void removeByID (int id) {
        Task currentTask = getTaskByID(id);
        if (currentTask == null) {
            return;
        }
        taskList.remove(currentTask);
    }

    public void setTaskStatusByID (int id, TaskStatus status) {
        Task currentTask = getTaskByID(id);
        if (currentTask == null) {
            return;
        }
        if (currentTask.getParent() == null) {
            setStatusChild(currentTask, status);
            currentTask.setStatus(status);
        } else {
            currentTask.setStatus(status);
            setStatusParent(currentTask);
        }
    }

    public void printAllTasks () {
        for (Task task : taskList) {
            System.out.println(task);
        }
    }

    public ArrayList<Task> getAllChild(Task parent) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getParent().equals(parent)) {
                result.add(task);
            }
        }
        return result;
    }

    public ArrayList<Task> getAllParent () {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getParent() == null) {
                result.add(task);
            }
        }
        return result;
    }

    public ArrayList<Task> getAllTask () {
        return taskList;
    }

    public Task getTaskByID (int id) {
        for (Task task : taskList) {
            if (task.getId() == id) {
                return task;
            }
        }
        System.out.println("Не найдена задача с ID = " + id + ".");
        return null;
    }

    //устанавливает статус всем дочерним задачам
    private void setStatusChild (Task task, TaskStatus status) {
        ArrayList<Task> taskChildList = getAllChild(task);
        if (taskChildList.isEmpty()) {
            return;
        }
        for (Task child : taskChildList) {
            child.setStatus(status);
        }
    }

    //проверяет статусы дочерних задач и устанавливает статус родителя
    private void setStatusParent (Task parent) {
        ArrayList<Task> taskChildList = getAllChild(parent);
        if (taskChildList.isEmpty()) {
            return;
        }
        boolean isDone = false;
        boolean isNew = false;
        boolean isInProgress = false;
        for (Task child : taskChildList) {
            switch (child.getStatus()) {
                case NEW -> isNew = true;
                case DONE -> isDone = true;
                case IN_PROGRESS -> isInProgress = true;
                default -> {
                    return;
                }
            }
            if (isDone && !isNew && !isInProgress) {
                parent.setStatus(TaskStatus.DONE);
            } else if (isNew && !isDone && !isInProgress) {
                parent.setStatus(TaskStatus.NEW);
            } else {
                parent.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
